-- RailShift PostgreSQL schema (PostgreSQL 14+)
-- Covers: Passenger app, TTE app, live train data cache (RailRadar), notifications.
-- Run once:  psql -d railshift -f schema.sql   (safe to re-run)

BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;   -- gen_random_uuid()

-- ---------- helpers ----------
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ---------- users & wallet ----------
CREATE TABLE IF NOT EXISTS users (
    id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name           text        NOT NULL,
    email          text        NOT NULL,
    password_hash  text        NOT NULL,
    role           text        NOT NULL DEFAULT 'passenger'
                   CHECK (role IN ('passenger', 'tte', 'admin')),
    language       text        NOT NULL DEFAULT 'en' CHECK (language IN ('en', 'hi', 'te')),
    theme_mode     text        NOT NULL DEFAULT 'SYSTEM' CHECK (theme_mode IN ('SYSTEM', 'LIGHT', 'DARK')),
    is_active      boolean     NOT NULL DEFAULT true,
    created_at     timestamptz NOT NULL DEFAULT now(),
    updated_at     timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS users_email_lower_uq ON users (lower(email));

CREATE TABLE IF NOT EXISTS wallets (
    user_id     uuid PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    balance     numeric(10,2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
    updated_at  timestamptz   NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS wallet_transactions (
    id          bigserial PRIMARY KEY,
    user_id     uuid          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount      numeric(10,2) NOT NULL,               -- +credit / -debit
    kind        text          NOT NULL CHECK (kind IN ('recharge', 'booking', 'refund', 'adjustment')),
    booking_id  uuid,                                 -- FK added after bookings exists
    created_at  timestamptz   NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS wallet_tx_user_idx ON wallet_transactions (user_id, created_at DESC);

CREATE TABLE IF NOT EXISTS device_tokens (            -- FCM push tokens
    token       text PRIMARY KEY,
    user_id     uuid        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS device_tokens_user_idx ON device_tokens (user_id);

-- ---------- stations, trains, schedules ----------
CREATE TABLE IF NOT EXISTS stations (
    code  text PRIMARY KEY,
    name  text NOT NULL,
    city  text NOT NULL,
    lat   double precision,
    lon   double precision
);

CREATE TABLE IF NOT EXISTS trains (
    number            text PRIMARY KEY,
    name              text    NOT NULL,
    from_station      text    NOT NULL REFERENCES stations(code),
    to_station        text    NOT NULL REFERENCES stations(code),
    departure_time    time    NOT NULL,
    arrival_time      time    NOT NULL,
    duration_minutes  integer NOT NULL CHECK (duration_minutes > 0),
    runs_on           text    NOT NULL DEFAULT 'Daily'
);

CREATE TABLE IF NOT EXISTS train_stops (              -- route / timetable
    train_number  text    NOT NULL REFERENCES trains(number) ON DELETE CASCADE,
    stop_seq      integer NOT NULL,
    station_code  text    NOT NULL REFERENCES stations(code),
    arrival_time  time,
    departure_time time,
    day_offset    integer NOT NULL DEFAULT 0,
    distance_km   integer,
    platform      text,
    PRIMARY KEY (train_number, stop_seq)
);
CREATE INDEX IF NOT EXISTS train_stops_station_idx ON train_stops (station_code);

CREATE TABLE IF NOT EXISTS train_classes (
    train_number  text    NOT NULL REFERENCES trains(number) ON DELETE CASCADE,
    class_code    text    NOT NULL CHECK (class_code IN ('SL', '3A', '2A', '1A', 'CC')),
    base_fare     integer NOT NULL CHECK (base_fare >= 0),
    total_berths  integer NOT NULL CHECK (total_berths >= 0),
    PRIMARY KEY (train_number, class_code)
);

CREATE TABLE IF NOT EXISTS seat_inventory (           -- availability per run date
    train_number     text    NOT NULL,
    journey_date     date    NOT NULL,
    class_code       text    NOT NULL,
    available_berths integer NOT NULL CHECK (available_berths >= 0),
    PRIMARY KEY (train_number, journey_date, class_code),
    FOREIGN KEY (train_number, class_code) REFERENCES train_classes(train_number, class_code) ON DELETE CASCADE
);

-- ---------- paper tickets (for the Upgrade flow) ----------
CREATE TABLE IF NOT EXISTS paper_tickets (
    ticket_number    text PRIMARY KEY,
    from_station     text    NOT NULL REFERENCES stations(code),
    to_station       text    NOT NULL REFERENCES stations(code),
    passengers_count integer NOT NULL CHECK (passengers_count > 0),
    fare_paid        integer NOT NULL CHECK (fare_paid >= 0),
    is_valid         boolean NOT NULL DEFAULT true,
    created_at       timestamptz NOT NULL DEFAULT now()
);

-- ---------- bookings ----------
CREATE TABLE IF NOT EXISTS bookings (
    id                  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    pnr                 text        NOT NULL UNIQUE CHECK (pnr ~ '^[0-9]{10}$'),
    user_id             uuid        NOT NULL REFERENCES users(id),
    ticket_type         text        NOT NULL
                        CHECK (ticket_type IN ('RESERVED', 'UNRESERVED', 'PLATFORM', 'UPGRADE')),
    train_number        text        REFERENCES trains(number),
    journey_date        date        NOT NULL,
    from_station        text        NOT NULL REFERENCES stations(code),
    to_station          text        NOT NULL REFERENCES stations(code),
    class_code          text        CHECK (class_code IN ('SL', '3A', '2A', '1A', 'CC')),
    status              text        NOT NULL DEFAULT 'CONFIRMED'
                        CHECK (status IN ('CONFIRMED', 'COMPLETED', 'WAITLISTED', 'CANCELLED')),
    coach               text,
    berth               text,
    passengers_count    integer     NOT NULL DEFAULT 1 CHECK (passengers_count > 0),
    total_fare          numeric(10,2) NOT NULL CHECK (total_fare >= 0),
    paper_ticket_number text        REFERENCES paper_tickets(ticket_number),
    created_at          timestamptz NOT NULL DEFAULT now(),
    updated_at          timestamptz NOT NULL DEFAULT now(),
    CHECK (ticket_type <> 'RESERVED' OR (train_number IS NOT NULL AND class_code IS NOT NULL)),
    CHECK (ticket_type <> 'UPGRADE'  OR paper_ticket_number IS NOT NULL)
);
CREATE INDEX IF NOT EXISTS bookings_user_idx  ON bookings (user_id, journey_date DESC);
CREATE INDEX IF NOT EXISTS bookings_train_idx ON bookings (train_number, journey_date);

DO $$ BEGIN
    ALTER TABLE wallet_transactions
        ADD CONSTRAINT wallet_tx_booking_fk FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL;
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

CREATE TABLE IF NOT EXISTS booking_passengers (       -- who is travelling (TTE passenger list)
    id          bigserial PRIMARY KEY,
    booking_id  uuid    NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    name        text    NOT NULL,
    age         integer CHECK (age BETWEEN 0 AND 120),
    gender      text    CHECK (gender IN ('M', 'F', 'O')),
    is_child    boolean NOT NULL DEFAULT false,
    coach       text,
    berth       text,
    status      text    NOT NULL DEFAULT 'CONFIRMED'
                CHECK (status IN ('CONFIRMED', 'WAITLISTED', 'CANCELLED', 'BOARDED', 'NO_SHOW'))
);
CREATE INDEX IF NOT EXISTS booking_passengers_booking_idx ON booking_passengers (booking_id);

-- ---------- live train data ----------
CREATE TABLE IF NOT EXISTS live_train_status (        -- latest position per running train (from RailRadar)
    train_number     text        NOT NULL,            -- no FKs: live data may include trains/stations we don't store
    journey_date     date        NOT NULL,
    train_name       text,
    status           text,
    current_station  text,
    next_station     text,
    lat              double precision,                -- last known station coordinates
    lon              double precision,
    speed_kmh        double precision,
    segment_progress double precision,
    delay_minutes    integer     NOT NULL DEFAULT 0,
    eta_next         timestamptz,
    platform         text,
    payload          jsonb,                           -- full RailRadar response (route, halts, exceptions)
    updated_at       timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (train_number, journey_date)
);

CREATE TABLE IF NOT EXISTS live_status_history (      -- logs & history
    id             bigserial PRIMARY KEY,
    train_number   text        NOT NULL,
    journey_date   date        NOT NULL,
    station_code   text,
    delay_minutes  integer,
    lat            double precision,
    lon            double precision,
    recorded_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS live_history_train_idx ON live_status_history (train_number, journey_date, recorded_at DESC);

-- Free RailRadar plan = ~1000 requests/month: cache every response, count every call.
CREATE TABLE IF NOT EXISTS api_cache (
    cache_key   text PRIMARY KEY,
    payload     jsonb       NOT NULL,
    fetched_at  timestamptz NOT NULL DEFAULT now(),
    expires_at  timestamptz NOT NULL
);
CREATE INDEX IF NOT EXISTS api_cache_expiry_idx ON api_cache (expires_at);

CREATE TABLE IF NOT EXISTS api_usage (
    month       date NOT NULL,                        -- first day of month
    provider    text NOT NULL DEFAULT 'railradar',
    requests    integer NOT NULL DEFAULT 0,
    PRIMARY KEY (month, provider)
);

-- ---------- TTE operations, notifications, misc ----------
CREATE TABLE IF NOT EXISTS tte_actions (
    id            bigserial PRIMARY KEY,
    tte_user_id   uuid        NOT NULL REFERENCES users(id),
    booking_id    uuid        REFERENCES bookings(id) ON DELETE SET NULL,
    passenger_id  bigint      REFERENCES booking_passengers(id) ON DELETE SET NULL,
    action        text        NOT NULL CHECK (action IN ('TICKET_CHECKED', 'BOARDED', 'NO_SHOW', 'BERTH_CHANGED', 'ALERT_RAISED')),
    note          text,
    created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS tte_actions_booking_idx ON tte_actions (booking_id);

CREATE TABLE IF NOT EXISTS notifications (
    id          bigserial PRIMARY KEY,
    user_id     uuid        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    kind        text        NOT NULL CHECK (kind IN ('DELAY', 'PLATFORM_CHANGE', 'CHART_PREPARED', 'BOOKING', 'GENERAL')),
    title       text        NOT NULL,
    body        text        NOT NULL,
    is_read     boolean     NOT NULL DEFAULT false,
    created_at  timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS notifications_user_idx ON notifications (user_id, is_read, created_at DESC);

CREATE TABLE IF NOT EXISTS recent_searches (
    id          bigserial PRIMARY KEY,
    user_id     uuid        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    from_station text       NOT NULL REFERENCES stations(code),
    to_station   text       NOT NULL REFERENCES stations(code),
    journey_date date       NOT NULL,
    class_code   text,
    created_at  timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS recent_searches_user_idx ON recent_searches (user_id, created_at DESC);

-- ---------- updated_at triggers ----------
DO $$
DECLARE t text;
BEGIN
    FOREACH t IN ARRAY ARRAY['users', 'wallets', 'bookings', 'live_train_status'] LOOP
        EXECUTE format('DROP TRIGGER IF EXISTS %I_set_updated_at ON %I', t, t);
        EXECUTE format('CREATE TRIGGER %I_set_updated_at BEFORE UPDATE ON %I
                        FOR EACH ROW EXECUTE FUNCTION set_updated_at()', t, t);
    END LOOP;
END $$;

COMMIT;

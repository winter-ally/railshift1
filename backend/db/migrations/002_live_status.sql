-- Run this ONCE in the Supabase SQL Editor (you already ran schema.sql).
-- Live data comes from RailRadar and may include trains/stations that are not in our own
-- tables, so the live tables must not require matching rows. Also stores the full response.
BEGIN;
ALTER TABLE live_train_status DROP CONSTRAINT IF EXISTS live_train_status_train_number_fkey;
ALTER TABLE live_train_status DROP CONSTRAINT IF EXISTS live_train_status_current_station_fkey;
ALTER TABLE live_train_status DROP CONSTRAINT IF EXISTS live_train_status_next_station_fkey;
ALTER TABLE live_train_status ADD COLUMN IF NOT EXISTS train_name text;
ALTER TABLE live_train_status ADD COLUMN IF NOT EXISTS status text;
ALTER TABLE live_train_status ADD COLUMN IF NOT EXISTS speed_kmh double precision;
ALTER TABLE live_train_status ADD COLUMN IF NOT EXISTS segment_progress double precision;
ALTER TABLE live_train_status ADD COLUMN IF NOT EXISTS payload jsonb;
ALTER TABLE live_train_status ENABLE ROW LEVEL SECURITY;
ALTER TABLE live_status_history ENABLE ROW LEVEL SECURITY;
COMMIT;

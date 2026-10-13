"""Live train status: serve from the database when fresh, otherwise call RailRadar (within quota)."""
import json
from datetime import datetime, timedelta, timezone

from . import railradar
from .config import settings
from .db import get_pool

IST = timezone(timedelta(hours=5, minutes=30))


class QuotaExhausted(Exception):
    pass


def parse_live(data: dict) -> dict:
    """Flatten a RailRadar `data` object into one row for live_train_status."""
    cur = data.get("currentLocation") or {}
    nxt = data.get("nextHalt") or {}
    stops = {s.get("stationCode"): s for s in data.get("route") or []}
    here = stops.get(cur.get("stationCode")) or {}
    nxt_stop = stops.get(nxt.get("stationCode")) or {}
    return {
        "train_number": str(data["trainNumber"]),
        "journey_date": data["startDate"],
        "train_name": data.get("trainName"),
        "status": data.get("status"),
        "current_station": cur.get("stationCode"),
        "next_station": nxt.get("stationCode"),
        "lat": here.get("lat"),
        "lon": here.get("lng"),
        "speed_kmh": cur.get("speedKmh"),
        "segment_progress": cur.get("segmentProgress"),
        "delay_minutes": int(data.get("delayMinutes") or 0),
        "eta_next": nxt_stop.get("scheduledArrival"),
        "platform": nxt_stop.get("platform"),
        "payload": json.dumps(data),
    }


def _reserve_request(conn) -> bool:
    """Atomically count one RailRadar call; False if this month's cap is reached."""
    month = datetime.now(IST).date().replace(day=1)
    row = conn.execute(
        """INSERT INTO api_usage (month, provider, requests) VALUES (%s, 'railradar', 1)
           ON CONFLICT (month, provider) DO UPDATE SET requests = api_usage.requests + 1
           WHERE api_usage.requests < %s
           RETURNING requests""",
        (month, settings.monthly_cap),
    ).fetchone()
    return row is not None


def _store(conn, row: dict) -> None:
    conn.execute(
        """INSERT INTO live_train_status
             (train_number, journey_date, train_name, status, current_station, next_station, lat, lon,
              speed_kmh, segment_progress, delay_minutes, eta_next, platform, payload, updated_at)
           VALUES (%(train_number)s, %(journey_date)s, %(train_name)s, %(status)s, %(current_station)s,
                   %(next_station)s, %(lat)s, %(lon)s, %(speed_kmh)s, %(segment_progress)s,
                   %(delay_minutes)s, %(eta_next)s, %(platform)s, %(payload)s::jsonb, now())
           ON CONFLICT (train_number, journey_date) DO UPDATE SET
             train_name=EXCLUDED.train_name, status=EXCLUDED.status,
             current_station=EXCLUDED.current_station, next_station=EXCLUDED.next_station,
             lat=EXCLUDED.lat, lon=EXCLUDED.lon, speed_kmh=EXCLUDED.speed_kmh,
             segment_progress=EXCLUDED.segment_progress, delay_minutes=EXCLUDED.delay_minutes,
             eta_next=EXCLUDED.eta_next, platform=EXCLUDED.platform, payload=EXCLUDED.payload,
             updated_at=now()""",
        row,
    )
    conn.execute(
        """INSERT INTO live_status_history (train_number, journey_date, station_code, delay_minutes, lat, lon)
           VALUES (%(train_number)s, %(journey_date)s, %(current_station)s, %(delay_minutes)s, %(lat)s, %(lon)s)""",
        row,
    )


def _summary(row: dict, source: str, full: bool) -> dict:
    out = {
        "trainNumber": row["train_number"],
        "trainName": row["train_name"],
        "journeyDate": str(row["journey_date"]),
        "status": row["status"],
        "delayMinutes": row["delay_minutes"],
        "currentStation": row["current_station"],
        "nextStation": row["next_station"],
        "lat": row["lat"],
        "lon": row["lon"],
        "speedKmh": row["speed_kmh"],
        "segmentProgress": row["segment_progress"],
        "platform": row["platform"],
        "updatedAt": row["updated_at"].isoformat() if hasattr(row["updated_at"], "isoformat") else row["updated_at"],
        "source": source,  # "cache" | "live" | "stale"
    }
    if full:
        payload = row["payload"]
        out["route"] = (json.loads(payload) if isinstance(payload, str) else payload or {}).get("route", [])
    return out


def get_live_status(number: str, date: str | None = None, full: bool = False) -> dict:
    pool = get_pool()
    with pool.connection() as conn:
        q = "SELECT * FROM live_train_status WHERE train_number = %s"
        args: list = [number]
        if date:
            q += " AND journey_date = %s"
            args.append(date)
        cached = conn.execute(q + " ORDER BY updated_at DESC LIMIT 1", args).fetchone()

        if cached:
            age = datetime.now(timezone.utc) - cached["updated_at"]
            if age < timedelta(minutes=settings.cache_minutes):
                return _summary(cached, "cache", full)

        if not _reserve_request(conn):
            conn.commit()
            if cached:
                return _summary(cached, "stale", full)
            raise QuotaExhausted()
        conn.commit()  # keep the count even if the call below fails

        try:
            data = railradar.fetch_live(number, date)
        except railradar.RailRadarError:
            if cached:
                return _summary(cached, "stale", full)
            raise
        row = parse_live(data)
        _store(conn, row)
        fresh = conn.execute(
            "SELECT * FROM live_train_status WHERE train_number=%s AND journey_date=%s",
            (row["train_number"], row["journey_date"]),
        ).fetchone()
        return _summary(fresh, "live", full)


def quota_status() -> dict:
    month = datetime.now(IST).date().replace(day=1)
    with get_pool().connection() as conn:
        row = conn.execute(
            "SELECT requests FROM api_usage WHERE month=%s AND provider='railradar'", (month,)
        ).fetchone()
    used = row["requests"] if row else 0
    return {"month": str(month), "used": used, "cap": settings.monthly_cap, "remaining": max(0, settings.monthly_cap - used)}

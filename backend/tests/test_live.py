import os
import psycopg
import pytest
from fastapi.testclient import TestClient

DB = os.environ["TEST_DATABASE_URL"]
os.environ["DATABASE_URL"] = DB

from app import db, live, railradar  # noqa: E402
from app.config import settings  # noqa: E402
from app.main import app  # noqa: E402

SAMPLE = {
    "trainNumber": "12711", "trainName": "Pinakini Express", "startDate": "2026-10-08",
    "status": "RUNNING", "delayMinutes": 12,
    "currentLocation": {"stationCode": "BZA", "status": "DEPARTED", "speedKmh": 71.5, "segmentProgress": 0.4},
    "nextHalt": {"stationCode": "OGL", "stationName": "Ongole"},
    "route": [
        {"stationCode": "BZA", "lat": 16.51, "lng": 80.62, "platform": "2"},
        {"stationCode": "OGL", "lat": 15.50, "lng": 80.05, "scheduledArrival": "2026-10-08T08:10:00+05:30", "platform": "1"},
    ],
}
calls = []


@pytest.fixture(autouse=True)
def clean(monkeypatch):
    with psycopg.connect(DB, autocommit=True) as c:
        c.execute("TRUNCATE live_train_status, live_status_history, api_usage")
    calls.clear()
    monkeypatch.setattr(settings, "monthly_cap", 950)
    monkeypatch.setattr(settings, "cache_minutes", 10)
    monkeypatch.setattr(settings, "app_api_key", "")

    def fake(number, date=None):
        calls.append(number)
        return SAMPLE
    monkeypatch.setattr(railradar, "fetch_live", fake)
    yield


client = TestClient(app)


def test_live_then_cache():
    a = client.get("/trains/12711/live").json()
    assert a["source"] == "live" and a["delayMinutes"] == 12 and a["currentStation"] == "BZA"
    assert a["lat"] == 16.51 and a["speedKmh"] == 71.5 and a["platform"] == "1"
    b = client.get("/trains/12711/live").json()
    assert b["source"] == "cache" and len(calls) == 1
    assert client.get("/admin/quota").json()["used"] == 1


def test_full_returns_route():
    r = client.get("/trains/12711/live?full=true").json()
    assert [s["stationCode"] for s in r["route"]] == ["BZA", "OGL"]


def test_expired_cache_refetches(monkeypatch):
    client.get("/trains/12711/live")
    monkeypatch.setattr(settings, "cache_minutes", 0)
    assert client.get("/trains/12711/live").json()["source"] == "live"
    assert len(calls) == 2
    with psycopg.connect(DB) as c:
        assert c.execute("SELECT count(*) FROM live_status_history").fetchone()[0] == 2


def test_quota_cap_serves_stale_then_503(monkeypatch):
    monkeypatch.setattr(settings, "monthly_cap", 1)
    monkeypatch.setattr(settings, "cache_minutes", 0)
    assert client.get("/trains/12711/live").json()["source"] == "live"
    assert client.get("/trains/12711/live").json()["source"] == "stale"   # cap hit, old data served
    assert len(calls) == 1
    r = client.get("/trains/12345/live")                                   # nothing cached
    assert r.status_code == 503 and len(calls) == 1


def test_upstream_error_serves_stale_or_errors(monkeypatch):
    def boom(number, date=None):
        raise railradar.RailRadarError("down", 503)
    client.get("/trains/12711/live")
    monkeypatch.setattr(settings, "cache_minutes", 0)
    monkeypatch.setattr(railradar, "fetch_live", boom)
    assert client.get("/trains/12711/live").json()["source"] == "stale"
    assert client.get("/trains/99999/live").status_code == 502
    nf = lambda n, d=None: (_ for _ in ()).throw(railradar.RailRadarError("nf", 404))
    monkeypatch.setattr(railradar, "fetch_live", nf)
    assert client.get("/trains/88888/live").status_code == 404


def test_validation_and_app_key(monkeypatch):
    assert client.get("/trains/12/live").status_code == 400
    assert client.get("/trains/12711/live?date=bad").status_code == 422
    monkeypatch.setattr(settings, "app_api_key", "s3cret")
    assert client.get("/trains/12711/live").status_code == 401
    assert client.get("/trains/12711/live", headers={"X-App-Key": "s3cret"}).status_code == 200
    assert client.get("/health").json() == {"ok": True}

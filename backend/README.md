# RailShift backend (FastAPI)

Live train data from RailRadar, cached in Postgres (Supabase) so the free 1,000 requests/month last.

## How a request works
`GET /trains/{number}/live` → fresh row in DB (younger than `LIVE_CACHE_MINUTES`)? return it.
Otherwise call RailRadar (counted in `api_usage`, hard stop at `RAILRADAR_MONTHLY_CAP`), store, return.
If RailRadar is down or the cap is reached, the last stored status is returned with `"source": "stale"`.
`?full=true` adds the route with per-station delays/platforms. `GET /admin/quota` shows usage.

## Run locally
```bash
cd backend
pip install -r requirements.txt
cp .env.example .env        # fill in DATABASE_URL and RAILRADAR_API_KEY
uvicorn app.main:app --reload
# http://127.0.0.1:8000/docs
```
Database: run `db/schema.sql`, `db/supabase_security.sql`, and (if schema.sql was run before live data was added) `db/migrations/002_live_status.sql`.

## Tests
```bash
TEST_DATABASE_URL=postgresql://user@host/dbname pytest   # uses a throwaway DB; RailRadar is mocked
```
Note: the tests TRUNCATE the live tables, so never point them at your real database.

## Budget maths
1,000/month ≈ 32/day. With a 10-minute cache one tracked train costs at most 6 calls/hour while someone is watching it.

## Deploy on Render (free)
`render.yaml` at the repo root defines the service. In Render: **New → Blueprint** → pick this repo/branch → enter
`DATABASE_URL` and `RAILRADAR_API_KEY` → Apply. Render generates `APP_API_KEY` (view it under Environment).
Free services sleep after 15 min idle; point a free uptime monitor (e.g. UptimeRobot) at `/health` every 5-10 min.
`/health` also touches the database, which keeps a free Supabase project from pausing.

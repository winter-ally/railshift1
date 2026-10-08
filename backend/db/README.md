# RailShift database (PostgreSQL)

| File | Purpose |
|---|---|
| `schema.sql` | Creates all 19 tables (users, wallets, trains, bookings, live status, TTE actions, notifications, API cache). Safe to re-run. |
| `seed.sql` | Sample stations/trains/inventory from the app's fake data. Dev only. |
| `../deploy/setup_postgres.sh` | Installs and configures PostgreSQL on the Ubuntu Oracle VM. |
| `../deploy/backup_postgres.sh` | Nightly `pg_dump`, 7-day retention. |

## On the Oracle VM
```bash
git clone https://github.com/winter-ally/railshift1 && cd railshift1/backend/deploy
sudo bash setup_postgres.sh --seed     # drop --seed for production
sudo cat /etc/railshift/db.env         # DATABASE_URL for FastAPI
```
Postgres listens on localhost only, so the database is not exposed to the internet.

## Notes
- `api_cache` + `api_usage` exist so the backend can stay within RailRadar's ~1000 requests/month free limit.
- Never commit `/etc/railshift/db.env` or real passwords.

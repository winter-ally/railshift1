#!/usr/bin/env bash
# One-shot PostgreSQL setup for the RailShift backend on an Ubuntu Oracle Cloud VM.
# Usage (on the VM):  sudo bash setup_postgres.sh [--seed]
# Safe to re-run: it keeps the existing database user/password.
set -euo pipefail

DB_NAME="railshift"
DB_USER="railshift_app"
ENV_DIR="/etc/railshift"
ENV_FILE="$ENV_DIR/db.env"
HERE="$(cd "$(dirname "$0")" && pwd)"
SCHEMA="$HERE/../db/schema.sql"
SEED="$HERE/../db/seed.sql"

[ "$(id -u)" -eq 0 ] || { echo "Run with sudo."; exit 1; }
[ -f "$SCHEMA" ] || { echo "schema.sql not found at $SCHEMA"; exit 1; }

echo "==> Installing PostgreSQL"
apt-get update -y
DEBIAN_FRONTEND=noninteractive apt-get install -y postgresql postgresql-contrib

systemctl enable --now postgresql

# Keep Postgres private: listen on localhost only (FastAPI runs on the same VM).
CONF="$(sudo -u postgres psql -Atc 'SHOW config_file')"
if ! grep -q "^listen_addresses *= *'localhost'" "$CONF"; then
    sed -i "s/^#\?listen_addresses.*/listen_addresses = 'localhost'/" "$CONF"
    systemctl restart postgresql
fi

echo "==> Creating database and user"
mkdir -p "$ENV_DIR"; chmod 750 "$ENV_DIR"
if [ -f "$ENV_FILE" ]; then
    # shellcheck disable=SC1090
    . "$ENV_FILE"
    DB_PASS="${DB_PASSWORD}"
else
    DB_PASS="$(openssl rand -hex 24)"
fi

sudo -u postgres psql -v ON_ERROR_STOP=1 <<SQL
DO \$\$ BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '$DB_USER') THEN
    CREATE ROLE $DB_USER LOGIN PASSWORD '$DB_PASS';
  ELSE
    ALTER ROLE $DB_USER PASSWORD '$DB_PASS';
  END IF;
END \$\$;
SQL
sudo -u postgres psql -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'" | grep -q 1 \
    || sudo -u postgres createdb -O "$DB_USER" "$DB_NAME"

echo "==> Applying schema"
# Extensions need superuser; the app user then owns every table.
sudo -u postgres psql -d "$DB_NAME" -c "CREATE EXTENSION IF NOT EXISTS pgcrypto"
cp "$SCHEMA" /tmp/rs_schema.sql; chmod 644 /tmp/rs_schema.sql
sudo -u postgres psql -d "$DB_NAME" -v ON_ERROR_STOP=1 -q -c "SET ROLE $DB_USER" -f /tmp/rs_schema.sql
rm -f /tmp/rs_schema.sql

if [ "${1:-}" = "--seed" ]; then
    echo "==> Loading sample data"
    cp "$SEED" /tmp/rs_seed.sql; chmod 644 /tmp/rs_seed.sql
    sudo -u postgres psql -d "$DB_NAME" -v ON_ERROR_STOP=1 -q -c "SET ROLE $DB_USER" -f /tmp/rs_seed.sql
    rm -f /tmp/rs_seed.sql
fi

umask 077
cat > "$ENV_FILE" <<ENV
DB_PASSWORD=$DB_PASS
DATABASE_URL=postgresql://$DB_USER:$DB_PASS@127.0.0.1:5432/$DB_NAME
ENV
chmod 640 "$ENV_FILE"

echo
echo "Done. Tables: $(sudo -u postgres psql -d "$DB_NAME" -Atc "select count(*) from information_schema.tables where table_schema='public'")"
echo "Connection string saved in $ENV_FILE (keep it secret, never commit it)."
echo "In FastAPI:  DATABASE_URL=\$(grep ^DATABASE_URL $ENV_FILE | cut -d= -f2-)"

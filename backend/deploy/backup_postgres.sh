#!/usr/bin/env bash
# Nightly PostgreSQL backup. Keeps the last 7 days on the VM disk.
# Optional: if the OCI CLI is configured and OCI_BUCKET is set, also uploads to Object Storage.
# Install cron:  echo '30 2 * * * root /opt/railshift/backend/deploy/backup_postgres.sh' | sudo tee /etc/cron.d/railshift-backup
set -euo pipefail

BACKUP_DIR="/var/backups/railshift"
DB_NAME="railshift"
STAMP="$(date +%F_%H%M)"
FILE="$BACKUP_DIR/${DB_NAME}_$STAMP.sql.gz"

mkdir -p "$BACKUP_DIR"; chmod 700 "$BACKUP_DIR"
sudo -u postgres pg_dump "$DB_NAME" | gzip > "$FILE"
find "$BACKUP_DIR" -name "${DB_NAME}_*.sql.gz" -mtime +7 -delete
echo "Backup written: $FILE ($(du -h "$FILE" | cut -f1))"

if [ -n "${OCI_BUCKET:-}" ] && command -v oci >/dev/null 2>&1; then
    oci os object put --bucket-name "$OCI_BUCKET" --file "$FILE" --force >/dev/null
    echo "Uploaded to Object Storage bucket $OCI_BUCKET"
fi

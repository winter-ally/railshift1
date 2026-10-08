from psycopg.rows import dict_row
from psycopg_pool import ConnectionPool

from .config import settings

_pool: ConnectionPool | None = None


def get_pool() -> ConnectionPool:
    global _pool
    if _pool is None:
        if not settings.database_url:
            raise RuntimeError("DATABASE_URL is not set")
        # prepare_threshold=None: safe with Supabase's pooler.
        _pool = ConnectionPool(
            settings.database_url,
            min_size=1,
            max_size=5,
            kwargs={"row_factory": dict_row, "prepare_threshold": None, "connect_timeout": 8},
            open=True,
            timeout=10,   # fail fast instead of hanging when the database is unreachable
        )
    return _pool


def close_pool() -> None:
    global _pool
    if _pool is not None:
        _pool.close()
        _pool = None

import re
from contextlib import asynccontextmanager

from fastapi import Depends, FastAPI, Header, HTTPException, Query

from . import live, railradar
from .config import settings
from .db import close_pool, get_pool

@asynccontextmanager
async def lifespan(_: FastAPI):
    yield
    close_pool()


app = FastAPI(title="RailShift API", lifespan=lifespan)


def require_app_key(x_app_key: str | None = Header(default=None)) -> None:
    if settings.app_api_key and x_app_key != settings.app_api_key:
        raise HTTPException(status_code=401, detail="Invalid or missing X-App-Key")


@app.get("/health")
def health() -> dict:
    with get_pool().connection() as conn:
        conn.execute("SELECT 1")
    return {"ok": True}


@app.get("/trains/{number}/live", dependencies=[Depends(require_app_key)])
def train_live(
    number: str,
    date: str | None = Query(default=None, pattern=r"^\d{4}-\d{2}-\d{2}$"),
    full: bool = False,
) -> dict:
    if not re.fullmatch(r"\d{5}", number):
        raise HTTPException(status_code=400, detail="Train number must be 5 digits")
    try:
        return live.get_live_status(number, date, full)
    except live.QuotaExhausted:
        raise HTTPException(status_code=503, detail="Monthly live-data quota used up; try again next month")
    except railradar.RailRadarError as e:
        code = 404 if e.status_code == 404 else 502
        raise HTTPException(status_code=code, detail=str(e))


@app.get("/admin/quota", dependencies=[Depends(require_app_key)])
def quota() -> dict:
    return live.quota_status()

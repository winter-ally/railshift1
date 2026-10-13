"""Thin client for RailRadar's live train endpoint (GET /v1/trains/{number}/live)."""
import httpx

from .config import settings


class RailRadarError(Exception):
    def __init__(self, message: str, status_code: int | None = None):
        super().__init__(message)
        self.status_code = status_code


def fetch_live(number: str, date: str | None = None) -> dict:
    """Returns the `data` object of the RailRadar response. Costs one request of the monthly quota."""
    if not settings.railradar_api_key:
        raise RailRadarError("RAILRADAR_API_KEY is not set")
    params = {"includeCoordinates": "true", "haltsOnly": "true"}
    if date:
        params["date"] = date
    try:
        r = httpx.get(
            f"{settings.railradar_base_url}/trains/{number}/live",
            params=params,
            headers={"Authorization": f"Bearer {settings.railradar_api_key}"},
            timeout=15,
        )
    except httpx.HTTPError as e:
        raise RailRadarError(f"RailRadar unreachable: {e}") from e
    if r.status_code != 200:
        raise RailRadarError(f"RailRadar returned {r.status_code}", r.status_code)
    body = r.json()
    if not body.get("success"):
        raise RailRadarError(str(body.get("error")), r.status_code)
    return body["data"]

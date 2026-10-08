import os
from dotenv import load_dotenv

load_dotenv()


class Settings:
    database_url: str = os.environ.get("DATABASE_URL", "")
    railradar_api_key: str = os.environ.get("RAILRADAR_API_KEY", "")
    railradar_base_url: str = os.environ.get("RAILRADAR_BASE_URL", "https://api.railradar.in/v1")
    monthly_cap: int = int(os.environ.get("RAILRADAR_MONTHLY_CAP", "950"))
    cache_minutes: int = int(os.environ.get("LIVE_CACHE_MINUTES", "10"))
    app_api_key: str = os.environ.get("APP_API_KEY", "")


settings = Settings()

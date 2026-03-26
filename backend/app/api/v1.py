import base64
import json
import os
from dataclasses import dataclass
from datetime import UTC, datetime, timedelta

from fastapi import APIRouter, Header, HTTPException, Query

router = APIRouter()


@dataclass
class CurrentUser:
    user_id: str


def _auth_required() -> bool:
    return os.getenv("AUTH_REQUIRED", "true").lower() in {"1", "true", "yes", "on"}


def _expected_token() -> str:
    return os.getenv("API_BEARER_TOKEN", "dev-aura-token")


def _legacy_raw_config_enabled() -> bool:
    return os.getenv("LEGACY_RAW_CONFIG", "true").lower() in {"1", "true", "yes", "on"}


def _build_short_lived_config_payload(user_id: str) -> dict:
    now = datetime.now(UTC)
    expires_at = now + timedelta(minutes=5)

    # MVP placeholder payload (non-secret metadata only).
    payload = {
        "sub": user_id,
        "scope": "vpn:config",
        "iat": int(now.timestamp()),
        "exp": int(expires_at.timestamp()),
    }
    token = base64.urlsafe_b64encode(json.dumps(payload).encode("utf-8")).decode("utf-8").rstrip("=")

    return {
        "config_token": token,
        "expires_at": expires_at.isoformat(),
        "config_preview": "vless://***@panel.kupisait1.ru:17799?...",
    }


def get_current_user(
    authorization: str | None = Header(default=None),
    user_id: str | None = Query(default=None),
) -> CurrentUser:
    """
    Minimal MVP auth layer.

    - Enabled by default (AUTH_REQUIRED=true)
    - Can be temporarily bypassed for rollback with AUTH_REQUIRED=false
    """
    if not _auth_required():
        # Backward compatibility mode for transition release:
        # if auth is disabled, allow legacy user_id query param.
        return CurrentUser(user_id=user_id or "mvp-debug")

    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="AUTH_REQUIRED")

    token = authorization.removeprefix("Bearer ").strip()
    if token != _expected_token():
        raise HTTPException(status_code=401, detail="AUTH_INVALID")

    return CurrentUser(user_id="ira-android")


@router.get("/profile")
def profile(
    authorization: str | None = Header(default=None),
    user_id: str | None = Query(default=None),
):
    user = get_current_user(authorization, user_id)
    return {"status": "ok", "subscription": "active", "plan": "mvp", "user_id": user.user_id}


@router.get("/vpn/config")
def vpn_config(
    authorization: str | None = Header(default=None),
    user_id: str | None = Query(default=None),
):
    user = get_current_user(authorization, user_id)
    response = {
        "status": "ok",
        "user_id": user.user_id,
        **_build_short_lived_config_payload(user.user_id),
    }

    # Transition mode: keep legacy field for old clients until Android update ships.
    if _legacy_raw_config_enabled():
        response["config"] = None

    return response


@router.post("/vpn/reissue")
def vpn_reissue(
    authorization: str | None = Header(default=None),
    user_id: str | None = Query(default=None),
):
    user = get_current_user(authorization, user_id)
    # TODO: add rate-limit + revoke-before-issue logic
    raise HTTPException(status_code=501, detail="Not implemented yet")

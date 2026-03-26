#!/usr/bin/env bash
set -euo pipefail

API_BASE_URL="${API_BASE_URL:-https://api-aura.kupisait1.ru}"
API_BEARER_TOKEN="${API_BEARER_TOKEN:-dev-aura-token}"
SMOKE_USER_ID="${SMOKE_USER_ID:-smoke-android}"

log() { echo "[smoke] $*"; }

retry() {
  local attempts="$1"; shift
  local delay="$1"; shift
  local n=1
  while true; do
    if "$@"; then
      return 0
    fi
    if [ "$n" -ge "$attempts" ]; then
      return 1
    fi
    n=$((n+1))
    sleep "$delay"
  done
}

call() {
  local method="$1"; shift
  local path="$1"; shift
  curl -sS -X "$method" \
    -H "Authorization: Bearer ${API_BEARER_TOKEN}" \
    -H "Content-Type: application/json" \
    "${API_BASE_URL}${path}" "$@"
}

http_code() {
  local method="$1"; shift
  local path="$1"; shift
  curl -sS -o /tmp/aura_smoke_body.json -w "%{http_code}" -X "$method" \
    -H "Authorization: Bearer ${API_BEARER_TOKEN}" \
    -H "Content-Type: application/json" \
    "${API_BASE_URL}${path}" "$@"
}

assert_json_field() {
  local json="$1"; shift
  local expr="$1"; shift
  python3 - "$json" "$expr" <<'PY'
import json, sys
payload = json.loads(sys.argv[1])
expr = sys.argv[2]

# very small evaluator for top-level fields only
if expr not in payload or payload[expr] in (None, ""):
    raise SystemExit(1)
print(payload[expr])
PY
}

log "1/4 health"
retry 3 2 curl -fsS "${API_BASE_URL}/health" >/tmp/aura_health.json
python3 - <<'PY'
import json
obj = json.load(open('/tmp/aura_health.json'))
assert obj.get('ok') is True
PY

log "2/4 profile"
PROFILE_JSON="$(retry 3 2 call GET '/v1/profile')"
assert_json_field "$PROFILE_JSON" status >/dev/null

log "3/4 config payload"
CONFIG_JSON="$(retry 3 2 call GET '/v1/vpn/config')"
assert_json_field "$CONFIG_JSON" status >/dev/null

# Transition check: accept new secure fields OR legacy raw config.
python3 - "$CONFIG_JSON" <<'PY'
import json, sys
obj = json.loads(sys.argv[1])
has_new = bool(obj.get('config_token')) and bool(obj.get('expires_at'))
has_legacy = bool(obj.get('config'))
if not (has_new or has_legacy):
    raise SystemExit('Neither new secure payload nor legacy config present')
PY

log "4/4 reissue rate-limit behavior"
FIRST_CODE="$(http_code POST '/v1/vpn/reissue')"
if [ "$FIRST_CODE" = "200" ]; then
  SECOND_CODE="$(http_code POST '/v1/vpn/reissue')"
  if [ "$SECOND_CODE" != "429" ]; then
    log "expected second reissue to be 429, got ${SECOND_CODE}"
    cat /tmp/aura_smoke_body.json || true
    exit 1
  fi
elif [ "$FIRST_CODE" = "429" ]; then
  log "rate-limit already active; acceptable"
else
  log "unexpected first reissue code: ${FIRST_CODE}"
  cat /tmp/aura_smoke_body.json || true
  exit 1
fi

log "SMOKE OK"

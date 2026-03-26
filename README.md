# AURA VPN MVP (Android-first)

Стартовый каркас MVP для Android VPN-приложения с персональными ключами и безопасным перевыпуском.

## Текущий прогресс
- [x] Шаг 1: Базовая архитектура и контракты API
- [x] Шаг 2: Backend hardening (Bearer auth + transition mode)
- [x] Шаг 3: Android skeleton (Kotlin + Compose)
- [x] Шаг 4: Android security integration (Bearer + encrypted storage)
- [x] Шаг 5: Smoke-check + CI guard

## Структура
- `docs/` — архитектура, API-контракты, модели
- `backend/` — сервер выдачи/перевыпуска ключей
- `android/` — Android клиент

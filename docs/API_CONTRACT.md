# API v1 (черновик)

## POST /v1/auth/magic/verify
Вход по одноразовому коду.

## GET /v1/profile
Профиль пользователя + статус подписки.

## GET /v1/vpn/config
Возвращает текущий активный конфиг (short-lived signed payload).

## POST /v1/vpn/reissue
Перевыпуск ключа.

### Поведение /v1/vpn/reissue
1. Проверить лимиты.
2. Деактивировать текущий ключ.
3. Выпустить новый ключ.
4. Записать событие в `key_events`.
5. Вернуть новый конфиг.

### Ошибки
- `429 REISSUE_RATE_LIMIT`
- `403 SUBSCRIPTION_EXPIRED`
- `503 PANEL_UNAVAILABLE`

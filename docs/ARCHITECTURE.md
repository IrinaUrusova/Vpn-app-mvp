# Архитектура MVP

## Компоненты
1. **Android App**
   - Авторизация по токену
   - Connect/Disconnect
   - Кнопка «Починить подключение»
2. **API Backend**
   - Выдача активного конфига
   - Перевыпуск ключа (revoke + issue)
   - Лимиты перевыпуска
3. **DB (PostgreSQL)**
   - users, devices, vpn_keys, key_events
4. **VPN Panel Adapter**
   - Обёртка для x-ui/3x-ui API (или прямой sing-box provisioning)

## Безопасность
- 1 user = 1 active key
- revoke before issue
- rate-limit: 1 request / 10 min, до 5/сутки
- без логирования секретов/полных URL

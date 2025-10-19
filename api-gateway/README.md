# API Gateway

API Gateway для системы бронирования отелей.

## Функции

- Маршрутизация запросов к микросервисам
- JWT валидация
- CORS configuration
- Load balancing через Eureka
- Request/Response logging

## Порты

- **8080** - HTTP порт

## Маршруты

### Booking Service
- `/api/auth/**` - Аутентификация (публичный)
- `/api/bookings/**` - Бронирования (требует JWT)
- `/api/users/**` - Пользователи (требует JWT + ADMIN роль)

### Hotel Service
- `/api/hotels/**` - Отели (требует JWT)
- `/api/rooms/**` - Номера (требует JWT)

## Запуск

```bash
mvn spring-boot:run
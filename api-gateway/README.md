# API Gateway

API Gateway для системы бронирования отелей. Обеспечивает единую точку входа для всех микросервисов.

## Функции

- Маршрутизация запросов к микросервисам
- JWT валидация и аутентификация
- CORS configuration
- Load balancing через Eureka Service Discovery
- Request/Response logging
- Rate limiting и throttling
- Централизованная обработка ошибок

## Технологии

- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Spring Security
- JWT

## Порты

- **8080** - HTTP порт

## Маршруты

### Authentication Service
- `/api/auth/**` - Аутентификация и регистрация (публичный доступ)
  - `POST /api/auth/login` - Вход в систему
  - `POST /api/auth/register` - Регистрация пользователя

### Booking Service
- `/api/bookings/**` - Управление бронированиями (требует аутентификации)
  - `GET /api/bookings` - Получить список бронирований
  - `POST /api/bookings` - Создать бронирование
  - `GET /api/bookings/{id}` - Получить бронирование по ID
  - `PUT /api/bookings/{id}` - Обновить бронирование
  - `DELETE /api/bookings/{id}` - Отменить бронирование

### Hotel Service
- `/api/hotels/**` - Управление отелями (требует аутентификации)
  - `GET /api/hotels` - Получить список отелей
  - `GET /api/hotels/{id}` - Получить отель по ID
  - `POST /api/hotels` - Создать отель (только для администраторов)
  - `PUT /api/hotels/{id}` - Обновить отель (только для администраторов)
  - `DELETE /api/hotels/{id}` - Удалить отель (только для администраторов)

## Конфигурация

Конфигурация находится в `src/main/resources/application.yml`:

- Настройки маршрутизации
- Настройки безопасности
- Параметры подключения к Eureka Server
- CORS политики

## Запуск

### Локальный запуск
```bash
mvn spring-boot:run
```

### Docker
```
docker build -t api-gateway .
docker run -p 8080:8080 api-gateway
```
## Зависимости
Перед запуском убедитесь, что запущен:

- Eureka Server - Service Discovery

## Переменные окружения
- EUREKA_SERVER_URL - URL Eureka Server (по умолчанию: http://localhost:8761/eureka)
- JWT_SECRET - Секретный ключ для JWT валидации
- SERVER_PORT - Порт сервера (по умолчанию: 8080)

## Мониторинг
Доступные эндпоинты для мониторинга:

- /actuator/health - Статус здоровья сервиса
- /actuator/info - Информация о сервисе
- /actuator/gateway/routes - Список всех маршрутов


# Microservices Booking System
## Общее описание

Система бронирования отелей, построенная на Spring Boot с использованием микросервисной архитектуры.
Реализует полную цепочку — от регистрации пользователей и авторизации по JWT до резервирования номеров с двухфазным подтверждением и автоматической компенсацией при ошибках.

## Состав системы
- api-gateway	- Центральная точка входа, маршрутизация запросов, проверка JWT
- booking-service	Управление пользователями и бронированиями
- hotel-service	Управление отелями, номерами и резервами
- eureka-server	Сервис-дискавери для регистрации и поиска микросервисов

## Запуск

Запустить все сервисы:

`docker-compose up --build`

Сервисы будут доступны:

Gateway: http://localhost:8080

Booking: http://localhost:8081

Hotel: http://localhost:8082

Eureka: http://localhost:8761

Swagger UI:

- Booking: http://localhost:8081/swagger-ui.html

- Hotel: http://localhost:8082/swagger-ui.html

- Тестовые пользователи
  - **Admin**: username: `admin`, password: `password123`
  - **User**: username: `user`, password: `password123`

## Основные особенности реализации
### 1. JWT-аутентификация и роли

Реализована на уровне Gateway и сервисов.

Gateway фильтрует запросы, извлекая и проверяя Authorization: Bearer <token>.

Сервисы дополнительно проверяют JWT через JwtAuthenticationFilter.

Поддерживаются роли USER и ADMIN, разграничивающие права на создание/удаление отелей, бронирования и пользователей.

### 2. Микросервисная архитектура и сервис-дискавери

Все сервисы зарегистрированы в Eureka (@EnableDiscoveryClient).

Gateway маршрутизирует запросы по шаблонам:

/api/hotels/** и /api/rooms/** → HotelService

/api/bookings/** и /api/users/** и /api/auth/** → BookingService

Каждый сервис имеет собственную БД H2 (in-memory).

### 3. Базы данных и схемы

- BookingService: Таблица bookings с полями status, request_id, created_at

- HotelService: Таблицы rooms, reservations, hotels.
Поле times_booked используется для подсчёта популярности номеров
(Файлы: schema.sql, data.sql)

- наименования баз данных для просмотра в h2-console - jdbc:h2:mem:hoteldb, jdbc:h2:mem:bookingdb, пользователь sa

### 4. Двухшаговая согласованность (Saga Pattern)

Реализована цепочка бронирования с компенсацией:

- BookingService инициирует бронирование (статус PENDING).

- Делает запрос к HotelService /api/reservations/reserve — временное резервирование.

- Если резерв успешен — вызывается /api/reservations/confirm → статус CONFIRMED.

- Если при подтверждении или резервировании произошла ошибка — вызывается /api/reservations/cancel → статус CANCELLED.

Таким образом обеспечена атомарность на уровне бизнес-логики при отсутствии распределённых транзакций.

### 5. Идемпотентность запросов

Каждый запрос на бронирование содержит requestId (UUID).

В БД bookings это поле уникальное.

При повторной доставке запроса с тем же requestId возвращается ранее созданная запись без повторных действий.

### 6. Автоматический выбор номера и рекомендации

Если клиент не указал roomId, система автоматически запрашивает HotelService /api/rooms/recommend

Рекомендации сортируются по возрастанию times_booked, чтобы балансировать загрузку номеров
(Файлы: RoomRepository.java, RoomController.java, BookingService.java)

### 7. Коммуникация между сервисами

Используется OpenFeign для REST-вызовов.

### 8. Swagger и OpenAPI

Подключён springdoc-openapi во всех сервисах.

Эндпоинты /v3/api-docs и /swagger-ui.html доступны через Gateway.

### 9. Тестирование

Юнит-тесты с MockMvc для сценариев:

- успешного бронирования,

- отмены при ошибке HotelService,

- повторного запроса с тем же requestId,

- тайм-аута и компенсации.

### Технологии

Spring Boot 3.3+, Spring Cloud (Eureka, Gateway, OpenFeign)

Spring Security + JWT

H2 Database, Lombok, MapStruct, Swagger

Docker Compose для локального запуска

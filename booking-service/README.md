# Booking Service

Сервис управления бронированиями с аутентификацией и авторизацией.

## Функции

- Регистрация и авторизация пользователей (JWT)
- Создание и управление бронированиями
- Двухшаговая транзакция с Hotel Service
- Роли: USER и ADMIN
- CRUD операции для администраторов

## API Endpoints

### Аутентификация
- `POST /api/auth/register` - Регистрация
- `POST /api/auth/login` - Авторизация

### Бронирования
- `GET /api/bookings/my` - Мои бронирования (USER)
- `POST /api/bookings` - Создать бронирование (USER)
- `POST /api/bookings/{id}/cancel` - Отменить бронирование (USER)
- `GET /api/bookings` - Все бронирования (ADMIN)
- `DELETE /api/bookings/{id}` - Удалить бронирование (ADMIN)

### Пользователи (ADMIN)
- `GET /api/users` - Список пользователей
- `GET /api/users/{id}` - Пользователь по ID
- `PUT /api/users/{id}` - Обновить пользователя
- `DELETE /api/users/{id}` - Удалить пользователя

## Тестовые пользователи

- **Admin**: username: `admin`, password: `password123`
- **User**: username: `user`, password: `password123`

## Запуск

```bash
mvn spring-boot:run
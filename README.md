# spring

jdbc:h2:mem:hoteldb
jdbc:h2:mem:bookingdb



# Сборка и запуск всех сервисов
docker-compose up --build

# Или в фоновом режиме
docker-compose up -d --build

# Просмотр логов
docker-compose logs -f

# Остановка
docker-compose down

Проверка работы
Eureka Dashboard: http://localhost:8761
API Gateway: http://localhost:8080
Hotel Service: http://localhost:8082
Booking Service: http://localhost:8081

Регистрация
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }'

Авторизация
  curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

Использование JWT
  curl -X GET http://localhost:8080/api/bookings/my \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"



  5. Доступ к Swagger UI
После запуска сервисов:

Booking Service Swagger: http://localhost:8081/swagger-ui.html
Hotel Service Swagger: http://localhost:8082/swagger-ui.html
Через API Gateway:

http://localhost:8080/swagger-ui.html (если настроите проксирование)
6. Использование Swagger UI
Откройте Swagger UI
Нажмите на кнопку "Authorize" в правом верхнем углу
Введите JWT токен в формате: Bearer <ваш_токен>
Теперь все запросы будут отправляться с этим токеном
Swagger автоматически сгенерирует документацию для всех ваших контроллеров, включая:

BookingController
AuthController
UserController
HotelController
RoomController
ReservationController
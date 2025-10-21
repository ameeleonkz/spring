# Hotel Service

Микросервис для управления информацией об отелях в системе бронирования.

## Описание

Hotel Service предоставляет REST API для работы с данными об отелях, включая:
- Получение списка доступных отелей
- Получение информации о конкретном отеле
- Управление данными об отелях (создание, обновление, удаление)

## Технологии

- Spring Boot
- Spring Data JPA
- H2 Database (для разработки)
- Eureka Client (для service discovery)

## Запуск

### Локальный запуск

```sh
cd hotel-service
mvn clean package
mvn spring-boot:run
```

### Endpoints
- http://localhost:8082/api/hotels - API для работы с отелями
- http://localhost:8082/h2-console - Консоль H2 базы данных
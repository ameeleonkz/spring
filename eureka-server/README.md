# Eureka Server

Service Discovery сервер для микросервисной архитектуры системы бронирования отелей.

## Описание

Eureka Server обеспечивает регистрацию и обнаружение всех микросервисов в системе.

## Порты

- **8761** - HTTP порт для Eureka Server

## Запуск локально

```bash
mvn spring-boot:run

docker build -t eureka-server .
docker run -p 8761:8761 eureka-server
http://localhost:8761
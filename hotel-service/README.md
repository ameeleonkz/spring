cd hotel-service
mvn clean package
mvn spring-boot:run

docker build -t hotel-service .
docker run -p 8082:8082 hotel-service

http://localhost:8082/api/hotels
http://localhost:8082/h2-console
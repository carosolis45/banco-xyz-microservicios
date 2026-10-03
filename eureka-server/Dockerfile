FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY config-server/ ./config-server/
COPY eureka-server/ ./eureka-server/
COPY cuenta-service/ ./cuenta-service/
COPY transaccion-service/ ./transaccion-service/
COPY reporte-service/ ./reporte-service/

RUN mvn -pl eureka-server -am clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
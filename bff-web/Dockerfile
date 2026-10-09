FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY config-server/ ./config-server/
COPY eureka-server/ ./eureka-server/
COPY cuenta-service/ ./cuenta-service/
COPY transaccion-service/ ./transaccion-service/
COPY reporte-service/ ./reporte-service/
COPY batch-service/ ./batch-service/
COPY clientes-service/ ./clientes-service/
COPY bff-web/ ./bff-web/
COPY bff-movil/ ./bff-movil/
COPY bff-cajeros/ ./bff-cajeros/

RUN mvn -pl bff-web -am clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/bff-web/target/*.jar app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]

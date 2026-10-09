# ============================================
# Etapa 1: Build con Maven
# ============================================
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

RUN mvn -pl clientes-service -am clean package -DskipTests

# ============================================
# Etapa 2: Runtime
# ============================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/clientes-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
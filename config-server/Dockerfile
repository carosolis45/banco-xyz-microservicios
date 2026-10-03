# ============================================
# Etapa 1: Build con Maven
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar el POM padre
COPY pom.xml .

# Copiar TODOS los módulos (Maven los necesita aunque solo compilemos uno)
COPY config-server/ ./config-server/
COPY eureka-server/ ./eureka-server/
COPY cuenta-service/ ./cuenta-service/
COPY transaccion-service/ ./transaccion-service/
COPY reporte-service/ ./reporte-service/

# Compilar solo config-server (con sus dependencias)
RUN mvn -pl config-server -am clean package -DskipTests

# ============================================
# Etapa 2: Runtime
# ============================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/config-server/target/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
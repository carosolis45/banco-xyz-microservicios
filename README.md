# Banco XYZ - Microservicios con Spring Cloud

Proyecto de arquitectura de microservicios para el Banco XYZ, implementando patrones de Spring Cloud, OAuth2.0 con Keycloak, Spring Batch, Backend for Frontend (BFF), Docker y tolerancia a fallos con Resilience4j.

## Descripcion

El proyecto Banco XYZ implementa una arquitectura de microservicios distribuida que moderniza el sistema legacy basado en COBOL.

## Modulos del proyecto

| Modulo | Puerto | Descripcion |
|---|---|---|
| config-server | 8888 | Configuracion centralizada |
| eureka-server | 8761 | Descubrimiento de servicios |
| cuenta-service | 8081 | Microservicio de cuentas |
| transaccion-service | 8082 | Microservicio de pagos |
| reporte-service | 8083 | Microservicio de reportes |
| clientes-service | 8084 | Microservicio de clientes |
| batch-service | 8090 | Procesos batch |
| bff-web | 8091 | BFF canal Web |
| bff-movil | 8092 | BFF canal Movil |
| bff-cajeros | 8093 | BFF canal Cajeros |
| Keycloak | 9000 | Authorization Server |
| ActiveMQ | 61616 | Broker JMS |

## Estructura del proyecto
banco-xyz-microservicios/
├── config-server/
├── eureka-server/
├── cuenta-service/
├── transaccion-service/
├── reporte-service/
├── clientes-service/
├── batch-service/
├── bff-web/
├── bff-movil/
├── bff-cajeros/
├── docker-compose.yaml
├── instrucciones.md
├── despliegue.md
├── README.md
└── pom.xml

text

## Prerequisitos

- Java 17+
- Maven 3.9+
- Docker Desktop 24+

## Instalacion y Ejecucion

### 1. Levantar Keycloak y ActiveMQ

docker start keycloak activemq

### 2. Compilar el proyecto

mvn clean package -DskipTests

### 3. Construir las imagenes Docker

docker build -t banco-xyz/config-server:1.0.0 -f config-server/Dockerfile .
docker build -t banco-xyz/eureka-server:1.0.0 -f eureka-server/Dockerfile .
docker build -t banco-xyz/cuenta-service:1.0.0 -f cuenta-service/Dockerfile .
docker build -t banco-xyz/clientes-service:1.0.0 -f clientes-service/Dockerfile .
docker build -t banco-xyz/bff-web:1.0.0 -f bff-web/Dockerfile .
docker build -t banco-xyz/bff-movil:1.0.0 -f bff-movil/Dockerfile .
docker build -t banco-xyz/bff-cajeros:1.0.0 -f bff-cajeros/Dockerfile .

### 4. Levantar todo

docker compose up -d

## Documentacion adicional

- instrucciones.md - Guia paso a paso
- despliegue.md - Guia de despliegue en AWS

## Autor

Carolina Solis
Repositorio: https://github.com/carosolis45/banco-xyz-microservicios

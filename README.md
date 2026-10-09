# Banco XYZ - Microservicios con Spring Cloud

Proyecto de arquitectura de microservicios para el Banco XYZ, implementando patrones de **Spring Cloud**, **OAuth2.0 con Keycloak**, **Spring Batch**, **Backend for Frontend (BFF)**, **Docker** y **tolerancia a fallos con Resilience4j**.

## Descripcion

El proyecto **Banco XYZ** implementa una arquitectura de microservicios distribuida que moderniza el sistema legacy basado en COBOL. Incorpora:

- **OAuth2.0** con Keycloak como Authorization Server
- **Spring Cloud Config** para configuracion centralizada
- **Spring Cloud Netflix Eureka** para descubrimiento de servicios
- **Spring Batch** para procesos batch (reportes, intereses, estados de cuenta)
- **Backend for Frontend (BFF)** para los canales Web, Movil y Cajeros
- **Resilience4j** para tolerancia a fallos (Circuit Breaker)
- **JMS con ActiveMQ** para comunicacion asincrona
- **Docker + Docker Compose** para despliegue en contenedores

## Arquitectura

Sistema compuesto por 7 microservicios + 3 BFF + 2 servicios de infraestructura:

| Modulo | Puerto | Descripcion |
|---|---|---|
| config-server | 8888 | Configuracion centralizada |
| eureka-server | 8761 | Descubrimiento de servicios |
| cuenta-service | 8081 | Microservicio de cuentas |
| transaccion-service | 8082 | Microservicio de pagos |
| reporte-service | 8083 | Microservicio de reportes |
| clientes-service | 8084 | Microservicio de clientes |
| batch-service | 8090 | Procesos batch (Spring Batch) |
| bff-web | 8091 | BFF canal Web |
| bff-movil | 8092 | BFF canal Movil |
| bff-cajeros | 8093 | BFF canal Cajeros |
| Keycloak | 9000 | Authorization Server OAuth2 |
| ActiveMQ | 61616 | Broker de mensajeria JMS |

## Tecnologias

| Tecnologia | Version | Uso |
|---|---|---|
| Java | 17 | Lenguaje base |
| Spring Boot | 3.3.4 | Framework principal |
| Spring Cloud | 2023.0.3 | Config, Eureka, Gateway |
| Spring Security | 6.3.3 | OAuth2 Resource Server |
| Spring Batch | 5.1.2 | Procesos batch |
| Keycloak | 25.0.6 | Authorization Server |
| Spring Data JPA | 3.3.4 | Persistencia |
| H2 Database | 2.x | Base de datos en memoria |
| ActiveMQ | Classic | Mensajeria JMS |
| Resilience4j | 2.1.0 | Circuit Breaker |
| Docker | 24+ | Contenedores |
| Maven | 3.9+ | Build |

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

```powershell
docker start keycloak activemq
2. Compilar el proyecto
powershell
mvn clean package -DskipTests
3. Construir las imagenes Docker
powershell
docker build -t banco-xyz/config-server:1.0.0 -f config-server/Dockerfile .
docker build -t banco-xyz/eureka-server:1.0.0 -f eureka-server/Dockerfile .
docker build -t banco-xyz/cuenta-service:1.0.0 -f cuenta-service/Dockerfile .
docker build -t banco-xyz/transaccion-service:1.0.0 -f transaccion-service/Dockerfile .
docker build -t banco-xyz/reporte-service:1.0.0 -f reporte-service/Dockerfile .
docker build -t banco-xyz/clientes-service:1.0.0 -f clientes-service/Dockerfile .
docker build -t banco-xyz/bff-web:1.0.0 -f bff-web/Dockerfile .
docker build -t banco-xyz/bff-movil:1.0.0 -f bff-movil/Dockerfile .
docker build -t banco-xyz/bff-cajeros:1.0.0 -f bff-cajeros/Dockerfile .
4. Levantar todo con Docker Compose
powershell
docker compose up -d
5. Verificar
powershell
docker ps
Configuracion de OAuth2 con Keycloak

Paso 1: Crear Realm
Nombre: banco-xyz

Paso 2: Crear Client
Client ID: bff-client

Client authentication: ON

Direct access grants: ON

Paso 3: Crear Roles
ADMIN

USER

Paso 4: Crear Usuarios
admin / admin123 (rol ADMIN)

user / user123 (rol USER)

API Endpoints
cuenta-service (8081)
Metodo	Endpoint	Rol
GET	/api/cuentas/	Publico
GET	/api/cuentas	USER/ADMIN
POST	/api/cuentas	ADMIN
clientes-service (8084)
Metodo	Endpoint	Rol
GET	/api/clientes/	Publico
GET	/api/clientes	USER/ADMIN
GET	/api/clientes/{id}	USER/ADMIN
POST	/api/clientes	ADMIN
BFF Web (8091)
Metodo	Endpoint	Rol
GET	/bff/web/	Publico
GET	/bff/web/clientes	USER/ADMIN
GET	/bff/web/cuentas	USER/ADMIN
BFF Movil (8092)
Metodo	Endpoint	Rol
GET	/bff/movil/	Publico
GET	/bff/movil/clientes/resumen	USER/ADMIN
GET	/bff/movil/cuentas/saldo	USER/ADMIN
BFF Cajeros (8093)
Metodo	Endpoint	Rol
GET	/bff/cajeros/	Publico
GET	/bff/cajeros/cuenta/{id}/saldo	USER/ADMIN
POST	/bff/cajeros/cuenta/{id}/retiro	ADMIN
Procesos Batch (Spring Batch)
Jobs implementados
reporteTransaccionesJob: Reporte de transacciones diarias con deteccion de anomalias

calculoInteresesJob: Calculo de intereses mensuales por tipo de cuenta

estadosCuentaAnualesJob: Generacion de estados de cuenta anuales

Caracteristicas
Manejo de errores con faultTolerant()

Reintentos automaticos (retryLimit: 3)

Skip de registros invalidos (skipLimit: 10)

Paralelismo con chunk processing

Documentacion adicional
instrucciones.md - Guia paso a paso para ejecutar y probar

despliegue.md - Guia de despliegue en AWS

Autor
Carolina Solis

Repositorio: https://github.com/carosolis45/banco-xyz-microservicios

Curso: Desarrollo Backend III

Actividad: EFT - Desarrollo Backend Avanzado: Spring Cloud y Batch


## Repositorio

https://github.com/carosolis45/banco-xyz-microservicios

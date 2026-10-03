# Banco XYZ - Microservicios con Spring Cloud

Proyecto de arquitectura de microservicios para el Banco XYZ, implementando patrones de **Spring Cloud**, **OAuth2.0 con Keycloak**, **Docker**, **mensajería asíncrona con JMS** y **tolerancia a fallos con Resilience4j**.

---

## Tabla de Contenidos

- [Descripción](#-descripción)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Prerequisitos](#-prerequisitos)
- [Instalación y Ejecución](#-instalación-y-ejecución)
- [Configuración de OAuth2 con Keycloak](#-configuración-de-oauth2-con-keycloak)
- [API Endpoints](#-api-endpoints)
- [Pruebas de Seguridad](#-pruebas-de-seguridad)
- [Tolerancia a Fallos (Resilience4j)](#-tolerancia-a-fallos-resilience4j)
- [Mensajería Asíncrona (JMS)](#-mensajería-asíncrona-jms)
- [Evidencias de Ejecución](#-evidencias-de-ejecución)
- [Autor](#-autor)

---

## Descripción

El proyecto **Banco XYZ** implementa una arquitectura de microservicios distribuida que permite gestionar cuentas, transacciones y reportes bancarios. Incorpora:

- **OAuth2.0** con Keycloak como Authorization Server
- **Spring Cloud Config** para configuración centralizada
- **Spring Cloud Netflix Eureka** para descubrimiento de servicios
- **Resilience4j** para tolerancia a fallos (Circuit Breaker)
- **JMS con ActiveMQ** para comunicación asíncrona
- **Docker + Docker Compose** para despliegue en contenedores

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                      CLIENTE (Postman/curl)                 │
└──────────────────┬──────────────────────────────────────────┘
                   │ 1. Login (OAuth2 password grant)
                   ▼
┌─────────────────────────────────────────────────────────────┐
│              KEYCLOAK (Authorization Server)                │
│                     Puerto 9000                             │
│  - Realm: banco-xyz                                         │
│  - Client: bff-client                                       │
│  - Roles: ADMIN, USER                                       │
└──────────────────┬──────────────────────────────────────────┘
                   │ 2. Access Token (JWT)
                   ▼
┌─────────────────────────────────────────────────────────────┐
│              CLIENTE (guarda el token)                      │
└──────────────────┬──────────────────────────────────────────┘
                   │ 3. Request con Bearer Token
                   ▼
┌─────────────────────────────────────────────────────────────┐
│              MICROSERVICIOS (Resource Servers)              │
│  ┌──────────────┐  ┌──────────────────┐  ┌───────────────┐  │
│  │cuenta-service│  │transaccion-service│ │reporte-service│  │
│  │   :8081      │  │      :8082        │ │   :8083       │  │
│  └──────┬───────┘  └────────┬──────────┘  └───────┬───────┘  │
│         │                   │                     │          │
│         └───────────────────┴─────────────────────┘          │
│                             │                                │
│              Valida tokens JWT con JWKS de Keycloak          │
└─────────────────────────────────────────────────────────────┘
                   │
                   │ Comunicación interna
                   ▼
┌─────────────────────────────────────────────────────────────┐
│  EUREKA (:8761) │ CONFIG SERVER (:8888) │ ACTIVEMQ (:61616) │
└─────────────────────────────────────────────────────────────┘
```

---

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| **Java** | 17 | Lenguaje base |
| **Spring Boot** | 3.3.4 | Framework principal |
| **Spring Cloud** | 2023.0.3 | Config, Eureka, Circuit Breaker |
| **Spring Security** | 6.3.3 | OAuth2 Resource Server |
| **Keycloak** | 25.0.6 | Authorization Server OAuth2.0 |
| **Spring Data JPA** | 3.3.4 | Persistencia |
| **H2 Database** | 2.x | Base de datos en memoria |
| **ActiveMQ** | Classic | Mensajería JMS |
| **Resilience4j** | 2.1.0 | Circuit Breaker |
| **Docker** | 29.2.1 | Contenedores |
| **Docker Compose** | v2 | Orquestación |
| **Maven** | 3.9 | Build |

---

## Estructura del Proyecto

```
banco-xyz-microservicios/
├── config-server/                     # Spring Cloud Config Server
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── config/
│   │       └── application.yml        # Config compartida (OAuth2, Eureka, JPA)
│   └── Dockerfile
├── eureka-server/                     # Service Discovery
│   ├── src/main/resources/
│   │   └── application.yml
│   └── Dockerfile
├── cuenta-service/                    # Microservicio de cuentas
│   ├── src/main/java/com/bancoxyz/cuenta/
│   │   ├── config/SecurityConfig.java # OAuth2 Resource Server
│   │   ├── controller/CuentaController.java
│   │   ├── exception/GlobalExceptionHandler.java
│   │   ├── service/CuentaService.java
│   │   └── ...
│   ├── src/main/resources/application.yml
│   └── Dockerfile
├── transaccion-service/               # Microservicio de transacciones
│   ├── src/main/java/com/bancoxyz/transaccion/
│   │   ├── config/SecurityConfig.java
│   │   ├── controller/TransaccionController.java
│   │   └── ...
│   └── Dockerfile
├── reporte-service/                   # Microservicio de reportes
│   ├── src/main/java/com/bancoxyz/reporte/
│   │   ├── config/SecurityConfig.java
│   │   ├── controller/ReporteController.java
│   │   └── ...
│   └── Dockerfile
├── docker-compose.yaml                # Orquestación de servicios
├── pom.xml                            # POM padre multi-módulo
└── README.md
```

---

## Prerequisitos

### Software Requerido

- **Java 17+** ([descargar](https://adoptium.net/))
- **Maven 3.9+** ([descargar](https://maven.apache.org/))
- **Docker Desktop** ([descargar](https://www.docker.com/products/docker-desktop))

### Servicios Externos Requeridos

Estos servicios deben estar corriendo en Docker para que el sistema funcione:

#### Keycloak (puerto 9000)

```bash
docker run -d --name keycloak \
  -p 9000:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:25.0.6 start-dev
```

#### ActiveMQ (puertos 61616 y 8161)

```bash
docker run -d --name activemq \
  -p 61616:61616 \
  -p 8161:8161 \
  -e ACTIVEMQ_ADMIN_LOGIN=admin \
  -e ACTIVEMQ_ADMIN_PASSWORD=admin \
  apache/activemq-classic:latest
```

---

## Instalación y Ejecución

### Opción 1: Con Docker Compose (Recomendado)

#### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/banco-xyz-microservicios.git
cd banco-xyz-microservicios
```

#### 2. Levantar Keycloak y ActiveMQ

```bash
docker start keycloak activemq
```

#### 3. Construir las imágenes de los microservicios

```bash
docker build -t banco-xyz/config-server:1.0.0 -f config-server/Dockerfile .
docker build -t banco-xyz/eureka-server:1.0.0 -f eureka-server/Dockerfile .
docker build -t banco-xyz/cuenta-service:1.0.0 -f cuenta-service/Dockerfile .
docker build -t banco-xyz/transaccion-service:1.0.0 -f transaccion-service/Dockerfile .
docker build -t banco-xyz/reporte-service:1.0.0 -f reporte-service/Dockerfile .
```

#### 4. Levantar todos los servicios

```bash
docker compose up -d --force-recreate
```

#### 5. Verificar que todos los servicios estén corriendo

```bash
docker compose ps
```

**Salida esperada:**
```
NAME                  IMAGE                                 STATUS
config-server         banco-xyz/config-server:1.0.0         Up
cuenta-service        banco-xyz/cuenta-service:1.0.0        Up
eureka-server         banco-xyz/eureka-server:1.0.0         Up
reporte-service       banco-xyz/reporte-service:1.0.0       Up
transaccion-service   banco-xyz/transaccion-service:1.0.0   Up
```

---

### Opción 2: Ejecución Local (Desarrollo)

```bash
# Terminal 1 - Config Server
cd config-server && mvn spring-boot:run

# Terminal 2 - Eureka Server
cd eureka-server && mvn spring-boot:run

# Terminal 3 - cuenta-service
cd cuenta-service && mvn spring-boot:run

# Terminal 4 - transaccion-service
cd transaccion-service && mvn spring-boot:run

# Terminal 5 - reporte-service
cd reporte-service && mvn spring-boot:run
```

---

## Configuración de OAuth2 con Keycloak

### Paso 1: Crear el Realm `banco-xyz`

1. Acceder a http://localhost:9000
2. Login con `admin` / `admin`
3. Crear realm `banco-xyz`

### Paso 2: Crear el Client `bff-client`

| Campo | Valor |
|---|---|
| Client ID | `bff-client` |
| Client authentication | ON |
| Authorization | ON |
| Standard flow | ON |
| Direct access grants | ON |
| Valid redirect URIs | `*` |
| Web origins | `*` |

**Client Secret:** (copiar y guardar)

### Paso 3: Crear Roles

| Rol | Descripción |
|---|---|
| `ADMIN` | Administrador del sistema |
| `USER` | Usuario estándar |

### Paso 4: Crear Usuarios

| Username | Password | Rol |
|---|---|---|
| `admin` | `admin123` | `ADMIN` |
| `user` | `user123` | `USER` |

**Nota:** Desactivar `Temporary` al asignar la contraseña.

---

## API Endpoints

### Cuenta Service (Puerto 8081)

| Método | Endpoint | Rol Requerido |
|---|---|---|
| GET | `/api/cuentas/` | Público |
| GET | `/api/cuentas` | USER / ADMIN |
| GET | `/api/cuentas/{cuentaId}` | USER / ADMIN |
| GET | `/api/cuentas/tipo/{tipo}` | USER / ADMIN |
| POST | `/api/cuentas` | **ADMIN** |

### Transaccion Service (Puerto 8082)

| Método | Endpoint | Rol Requerido |
|---|---|---|
| GET | `/api/transacciones/` | Público |
| GET | `/api/transacciones` | USER / ADMIN |
| GET | `/api/transacciones/{id}` | USER / ADMIN |
| GET | `/api/transacciones/cuenta/{cuentaId}` | USER / ADMIN |
| GET | `/api/transacciones/anomalias` | USER / ADMIN |
| POST | `/api/transacciones` | USER / ADMIN |

### Reporte Service (Puerto 8083)

| Método | Endpoint | Rol Requerido |
|---|---|---|
| GET | `/api/reportes/` | Público |
| GET | `/api/reportes` | USER / ADMIN |
| GET | `/api/reportes/{id}` | USER / ADMIN |
| GET | `/api/reportes/cuenta/{cuentaId}` | USER / ADMIN |
| GET | `/api/reportes/cuenta/{cuentaId}/con-detalle` | USER / ADMIN |
| POST | `/api/reportes` | USER / ADMIN |

---

## Pruebas de Seguridad

### 1. Obtener Token de Keycloak (ADMIN)

```bash
curl -X POST "http://localhost:9000/realms/banco-xyz/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=bff-client" \
  -d "client_secret=TU_CLIENT_SECRET" \
  -d "username=admin" \
  -d "password=admin123" \
  -d "grant_type=password"
```

### 2. Listar Cuentas con Token

```bash
curl -H "Authorization: Bearer TU_ACCESS_TOKEN" \
  http://localhost:8081/api/cuentas
```

### 3. Crear Cuenta con ADMIN (201 Created)

```bash
curl -X POST http://localhost:8081/api/cuentas \
  -H "Authorization: Bearer TU_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"cuentaId":"TEST001","nombre":"Cuenta Test","saldo":1000,"edad":30,"tipo":"ahorro"}'
```

### 4. Intentar Crear Cuenta con USER (403 Forbidden)

```bash
curl -X POST http://localhost:8081/api/cuentas \
  -H "Authorization: Bearer TOKEN_USER" \
  -H "Content-Type: application/json" \
  -d '{"cuentaId":"TEST002","nombre":"Otra","saldo":100,"edad":25,"tipo":"ahorro"}'
```

**Resultado esperado:** `HTTP/1.1 403 Forbidden`

---

## Tolerancia a Fallos (Resilience4j)

El microservicio `reporte-service` implementa **Circuit Breaker** con Resilience4j para llamadas a `cuenta-service`.

### Configuración

```yaml
resilience4j:
  circuitbreaker:
    instances:
      cuentaService:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 5s
  timelimiter:
    instances:
      cuentaService:
        timeoutDuration: 3s
```

### Estados del Circuit Breaker

- **CLOSED:** Todo funciona normal
- **OPEN:** Se superó el umbral de fallos → bloquea llamadas temporalmente
- **HALF_OPEN:** Prueba con algunas llamadas para ver si el servicio se recuperó

---

## Mensajería Asíncrona (JMS)

Se utiliza **ActiveMQ** para comunicación asíncrona entre microservicios.

### Colas/Topics

| Nombre | Uso |
|---|---|
| `transaccion.creada` | Cuando se crea una transacción |
| `cuenta.actualizada` | Cuando se actualiza una cuenta |

### Consola de ActiveMQ

- **URL:** http://localhost:8161
- **Usuario:** `admin`
- **Contraseña:** `admin`

---

## Evidencias de Ejecución

Las capturas de las pruebas se encuentran en la carpeta `/capturas`:

| Captura | Descripción |
|---|---|
| `captura-s8-keycloak-roles.png` | Roles ADMIN y USER en Keycloak |
| `captura-s8-keycloak-users.png` | Usuarios admin y user |
| `captura-s8-token-admin.png` | Token obtenido vía curl |
| `captura-s8-cuenta-local-200.png` | OAuth2 funcionando (local) |
| `captura-s8-docker-compose-ps.png` | Los 5 servicios Docker corriendo |
| `captura-s8-docker-cuenta-con-token-200.png` | **OAuth2 funcionando en Docker (200 OK)** |
| `captura-s8-docker-cuenta-201-admin.png` | **Crear cuenta con ADMIN (201)** |
| `captura-s8-docker-cuenta-403-user.png` | **USER bloqueado (403)** |

---

## Limitaciones Conocidas

### Docker Desktop en Windows y Keycloak

En **Docker Desktop para Windows**, los contenedores no pueden resolver `localhost` como la máquina host. Se utiliza `host.docker.internal` para la comunicación.

**Configuración aplicada:**

- **Micros → Keycloak:** `http://host.docker.internal:9000/realms/banco-xyz`
- **Micros → ActiveMQ:** `tcp://host.docker.internal:61616`
- **Micros → Config Server:** `http://config-server:8888` (DNS interno de Docker)
- **Micros → Eureka:** `http://eureka-server:8761/eureka/` (DNS interno de Docker)

---

## Autor


**Carolina Solis** - [carosolis45](https://github.com/carosolis45)

- Curso: Desarrollo de Microservicios con Spring Cloud
- Semana 8: OAuth2.0 + Docker + Docker Compose

## Repositorio

https://github.com/carosolis45/banco-xyz-microservicios
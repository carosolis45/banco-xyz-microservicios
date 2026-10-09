Instrucciones de Ejecución y Prueba - Banco XYZ

Este documento describe paso a paso cómo ejecutar y probar todos los componentes del sistema **Banco XYZ Microservicios**.

---

## Índice

1. [Prerequisitos](#1-prerequisitos)
2. [Estructura del sistema](#2-estructura-del-sistema)
3. [Levantar la infraestructura](#3-levantar-la-infraestructura)
4. [Verificar Keycloak](#4-verificar-keycloak)
5. [Levantar todos los servicios](#5-levantar-todos-los-servicios)
6. [Verificar que todo funciona](#6-verificar-que-todo-funciona)
7. [Probar OAuth2 con token](#7-probar-oauth2-con-token)
8. [Probar los microservicios](#8-probar-los-microservicios)
9. [Probar los BFF](#9-probar-los-bff)
10. [Ejecutar Spring Batch](#10-ejecutar-spring-batch)
11. [Solución de problemas](#11-solución-de-problemas)

---

## 1. Prerequisitos

### Software requerido

| Software | Versión | Verificar con |
|---|---|---|
| **Java JDK** | 17+ | `java -version` |
| **Maven** | 3.9+ | `mvn -version` |
| **Docker Desktop** | 24+ | `docker --version` |
| **Git** | Cualquiera | `git --version` |

### Verificar instalación

```powershell
java -version
mvn -version
docker --version
git --version

2. Estructura del sistema
Módulos del proyecto
Módulo	Puerto	Descripción
config-server	8888	Servidor de configuración centralizada
eureka-server	8761	Servicio de descubrimiento
cuenta-service	8081	Microservicio de cuentas
transaccion-service	8082	Microservicio de pagos/transacciones
reporte-service	8083	Microservicio de reportes
clientes-service	8084	Microservicio de clientes
batch-service	8090	Procesos batch con Spring Batch
bff-web	8091	BFF para canal Web
bff-movil	8092	BFF para canal Móvil
bff-cajeros	8093	BFF para canal Cajeros
Keycloak	9000	Authorization Server (OAuth2)
ActiveMQ	61616	Broker de mensajería JMS

3. Levantar la infraestructura
3.1 Levantar Keycloak
powershell
docker run -d --name keycloak `
  -p 9000:8080 `
  -e KEYCLOAK_ADMIN=admin `
  -e KEYCLOAK_ADMIN_PASSWORD=admin `
  quay.io/keycloak/keycloak:25.0.6 start-dev
Verificar:

powershell
docker ps | findstr keycloak
Abrir en el navegador: http://localhost:9000

Usuario: admin

Contraseña: admin

3.2 Levantar ActiveMQ
powershell
docker run -d --name activemq `
  -p 61616:61616 `
  -p 8161:8161 `
  -e ACTIVEMQ_ADMIN_LOGIN=admin `
  -e ACTIVEMQ_ADMIN_PASSWORD=admin `
  apache/activemq-classic:latest
Verificar:

powershell
docker ps | findstr activemq
Consola web: http://localhost:8161 (admin/admin)

4. Verificar Keycloak
4.1 Configurar Realm banco-xyz
Abrir http://localhost:9000

Login: admin / admin

Crear nuevo Realm: banco-xyz

4.2 Crear Cliente bff-client
En el realm banco-xyz:

Ir a Clients → Create client

Client ID: bff-client

Client authentication: ON

Standard flow: ON

Direct access grants: ON

Valid redirect URIs: *

Web origins: *

Guardar

Anotar el Client Secret (pestaña Credentials)

4.3 Crear Roles
En Realm roles:

ADMIN - Administrador del sistema

USER - Usuario estándar

4.4 Crear Usuarios
En Users → Add user:

Usuario 1:

Username: admin

Email: admin@banco-xyz.cl

First name: Admin

Last name: Banco

Email verified: ON

Set password: admin123 (Temporary: OFF)

Role mapping: Asignar ADMIN

Usuario 2:

Username: user

Email: user@banco-xyz.cl

First name: User

Last name: Banco

Email verified: ON

Set password: user123 (Temporary: OFF)

Role mapping: Asignar USER

5. Levantar todos los servicios
5.1 Compilar todos los módulos
powershell
cd C:\ruta\al\proyecto
mvn clean package -DskipTests
Esperado: BUILD SUCCESS para todos los módulos.

5.2 Construir las imágenes Docker
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
5.3 Levantar todo con Docker Compose
powershell
docker compose up -d
Esperado:

text
[+] up XX/XX
 ✔ Container config-server       Started
 ✔ Container eureka-server       Started
 ✔ Container cuenta-service      Started
 ✔ Container transaccion-service Started
 ✔ Container reporte-service     Started
 ✔ Container clientes-service    Started
 ✔ Container bff-web             Started
 ✔ Container bff-movil           Started
 ✔ Container bff-cajeros         Started

6. Verificar que todo funciona
6.1 Verificar contenedores corriendo
powershell
docker ps
Esperado: 11 contenedores corriendo (9 servicios + keycloak + activemq).

6.2 Verificar Eureka
Abrir http://localhost:8761

Esperado: Ver los microservicios registrados:

CUENTA-SERVICE

TRANSACCION-SERVICE

REPORTE-SERVICE

CLIENTES-SERVICE

BFF-WEB

BFF-MOVIL

BFF-CAJEROS

6.3 Verificar Config Server
powershell
curl.exe http://localhost:8888/cuenta-service/default
Esperado: JSON con la configuración.

7. Probar OAuth2 con token
7.1 Obtener token de Keycloak (ADMIN)
powershell
$TOKEN = (curl.exe -s -X POST "http://host.docker.internal:9000/realms/banco-xyz/protocol/openid-connect/token" `
  -H "Content-Type: application/x-www-form-urlencoded" `
  -d "client_id=bff-client" `
  -d "client_secret=TU_CLIENT_SECRET" `
  -d "username=admin" `
  -d "password=admin123" `
  -d "grant_type=password" | ConvertFrom-Json).access_token

Write-Host "Token length: $($TOKEN.Length)"
Esperado: Token length > 800.

7.2 Decodificar el token
powershell
$payload = $TOKEN.Split('.')[1]
$payload = $payload.Replace('-','+').Replace('_','/')
while ($payload.Length % 4) { $payload += '=' }
[System.Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($payload)) | ConvertFrom-Json | Select iss, preferred_username, realm_access
Esperado:

text
iss: http://host.docker.internal:9000/realms/banco-xyz
preferred_username: admin
realm_access.roles: [ADMIN, ...]

8. Probar los microservicios
8.1 cuenta-service
Home público:

powershell
curl.exe http://localhost:8081/api/cuentas/
Listar cuentas con token:

powershell
curl.exe -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/cuentas
Esperado: 8 cuentas en JSON.

Sin token (debe dar 401):

powershell
curl.exe -i http://localhost:8081/api/cuentas

8.2 clientes-service
Home público:

powershell
curl.exe http://localhost:8084/api/clientes/
Listar clientes con token:

powershell
curl.exe -H "Authorization: Bearer $TOKEN" http://localhost:8084/api/clientes
Esperado: 7 clientes en JSON.

8.3 transaccion-service
Listar transacciones con token:

powershell
curl.exe -H "Authorization: Bearer $TOKEN" http://localhost:8082/api/transacciones

9. Probar los BFF
9.1 BFF Web (puerto 8091)
Home público:

powershell
curl.exe http://localhost:8091/bff/web/
Listar clientes (datos completos):

powershell
curl.exe -H "Authorization: Bearer $TOKEN" http://localhost:8091/bff/web/clientes
Esperado: 7 clientes con TODOS los campos (email, teléfono, dirección).

9.2 BFF Móvil (puerto 8092)
Home público:

powershell
curl.exe http://localhost:8092/bff/movil/
Listar clientes (respuesta ligera):

powershell
curl.exe -H "Authorization: Bearer $TOKEN" http://localhost:8092/bff/movil/clientes/resumen
Esperado: 7 clientes solo con id, nombre, tipo (sin datos extra).

9.3 BFF Cajeros (puerto 8093)
Home público:

powershell
curl.exe http://localhost:8093/bff/cajeros/
Consultar saldo de cuenta 101:

powershell
curl.exe -H "Authorization: Bearer $TOKEN" http://localhost:8093/bff/cajeros/cuenta/101/saldo
Esperado: Info de la cuenta con estructura de cajeros.

10. Ejecutar Spring Batch
10.1 Modificar el job a ejecutar
Editar batch-service/src/main/resources/application.yml:

yaml
batch:
  job:
    to-run: reporteTransaccionesJob    # Cambiar por el job deseado

10.2 Ejecutar el batch-service
powershell
cd batch-service
mvn spring-boot:run
Jobs disponibles:

reporteTransaccionesJob - Reporte de transacciones diarias

calculoInteresesJob - Cálculo de intereses mensuales

estadosCuentaAnualesJob - Estados de cuenta anuales

Esperado en el log:

text
Iniciando ejecución del job: reporteTransaccionesJob
Iniciando job: reporteTransaccionesJob
Procesadas 5 transacciones. Anomalías detectadas: 2
Job reporteTransaccionesJob completado exitosamente en XX ms

11. Solución de problemas
Error: "Port is already in use"
Causa: Otro proceso usa el puerto.

Solución:

powershell
# Ver qué proceso usa el puerto (ej. 8081)
netstat -ano | findstr :8081

# Matar el proceso (reemplazar PID)
taskkill /PID <PID> /F
Error: "Connection refused" al Config Server
Causa: Los micros intentan llegar a config-server:8888 fuera de Docker.

Solución: Ejecutar todo dentro de Docker con docker compose up -d.

Error 401 en endpoint con token
Causa: El issuer-uri del token no coincide.

Solución: Verificar que el token se pida con http://host.docker.internal:9000/... (NO localhost).

Docker Desktop no responde
Solución:

Clic derecho en el ícono 🐳 → Restart Docker Desktop

Esperar 1-2 minutos

Verificar con docker ps

Error: "no space left on device"
Solución:

powershell
docker system prune -a
Ojo: Elimina todas las imágenes no usadas.


Checklist de verificación final
□ Keycloak corriendo en 9000
□ ActiveMQ corriendo en 61616
□ Eureka corriendo en 8761
□ Config Server corriendo en 8888
□ 3 microservicios corriendo (8081, 8082, 8084)
□ 3 BFF corriendo (8091, 8092, 8093)
□ Token de Keycloak obtenido correctamente
□ Endpoints con token devuelven 200
□ Endpoints sin token devuelven 401
□ Los 3 BFF adaptan respuestas por canal
□ Spring Batch ejecuta los 3 jobs
□ Todos los logs muestran "Started ... in X seconds"


Autor: Carolina Solís
Repositorio: https://github.com/carosolis45/banco-xyz-microservicios
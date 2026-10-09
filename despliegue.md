\# Guia de Despliegue en AWS - Banco XYZ



Este documento describe los pasos para desplegar el sistema Banco XYZ Microservicios en Amazon Web Services (AWS).



Nota: El proyecto esta preparado para desplegarse en AWS, pero el despliegue efectivo queda como trabajo futuro.



\---



\## 1. Arquitectura en AWS



\### Servicios AWS utilizados



\- ECS Fargate: Ejecutar contenedores sin gestionar servidores

\- ECR: Registro de imagenes Docker

\- RDS PostgreSQL: Base de datos persistente

\- Application Load Balancer: Balanceo de carga

\- Route 53: DNS

\- CloudWatch: Logs y monitoreo

\- Secrets Manager: Gestion de credenciales



\### Diagrama



Internet -> Route 53 -> ALB -> ECS Cluster (10 servicios) -> RDS / Amazon MQ / Keycloak



\---



\## 2. Prerequisitos AWS



\### Cuenta AWS



\- Cuenta con permisos de administrador

\- Region recomendada: us-east-1

\- Presupuesto estimado: 50-100 USD/mes para ambiente de pruebas



\### Herramientas locales



Instalar AWS CLI:



winget install Amazon.AWSCLI



Configurar credenciales:



aws configure



\---



\## 3. Preparacion de imagenes Docker



\### Crear repositorio en ECR



aws ecr create-repository --repository-name banco-xyz/cuenta-service --region us-east-1



Repetir para cada servicio.



\### Autenticarse en ECR



aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin AWS\_ACCOUNT\_ID.dkr.ecr.us-east-1.amazonaws.com



\### Tag y push



docker tag banco-xyz/cuenta-service:1.0.0 AWS\_ACCOUNT\_ID.dkr.ecr.us-east-1.amazonaws.com/banco-xyz/cuenta-service:1.0.0

docker push AWS\_ACCOUNT\_ID.dkr.ecr.us-east-1.amazonaws.com/banco-xyz/cuenta-service:1.0.0



\---



\## 4. Despliegue de RDS



\### Crear instancia PostgreSQL



aws rds create-db-instance --db-instance-identifier banco-xyz-postgres --db-instance-class db.t3.micro --engine postgres --master-username admin --master-user-password CAMBIAR\_PASSWORD --allocated-storage 20 --backup-retention-period 7



\### Bases de datos a crear



\- keycloak\_db (para Keycloak)

\- batch\_db (para Spring Batch)



\---



\## 5. Despliegue de Keycloak en ECS



\### Crear cluster ECS



aws ecs create-cluster --cluster-name banco-xyz-cluster



\### Task definition para Keycloak



Configurar:

\- image: quay.io/keycloak/keycloak:25.0.6

\- puerto: 8080

\- variables: KC\_DB, KC\_DB\_URL, KC\_DB\_USERNAME, KC\_DB\_PASSWORD

\- comando: start-dev



\---



\## 6. Despliegue de microservicios en ECS Fargate



Para cada microservicio:



1\. Registrar task definition con imagen de ECR

2\. Configurar variables de entorno (Eureka, Keycloak)

3\. Crear servicio ECS con 1 tarea inicial

4\. Asociar al ALB



Servicios a desplegar:

\- config-server (8888)

\- eureka-server (8761)

\- cuenta-service (8081)

\- transaccion-service (8082)

\- reporte-service (8083)

\- clientes-service (8084)

\- batch-service (8090)

\- bff-web (8091)

\- bff-movil (8092)

\- bff-cajeros (8093)



\---



\## 7. Configuracion de ALB



\### Target groups



\- bff-web-tg -> puerto 8091

\- bff-movil-tg -> puerto 8092

\- bff-cajeros-tg -> puerto 8093

\- cuenta-service-tg -> puerto 8081



\### Reglas de enrutamiento



\- /bff/web/\* -> bff-web-tg

\- /bff/movil/\* -> bff-movil-tg

\- /bff/cajeros/\* -> bff-cajeros-tg

\- /api/cuentas/\* -> cuenta-service-tg



\---



\## 8. Configuracion de auto-scaling



\### Escalabilidad horizontal



| Servicio | Min | Max | Metrica |

|---|---|---|---|

| cuenta-service | 1 | 5 | CPU > 70% |

| transaccion-service | 1 | 5 | CPU > 70% |

| clientes-service | 1 | 5 | CPU > 70% |

| bff-web | 2 | 10 | CPU > 60% |

| bff-movil | 2 | 10 | CPU > 60% |

| bff-cajeros | 2 | 8 | CPU > 60% |



\### Politica de scaling



aws application-autoscaling register-scalable-target --service-namespace ecs --resource-id service/banco-xyz-cluster/cuenta-service --scalable-dimension ecs:service:DesiredCount --min-capacity 1 --max-capacity 5



\---



\## 9. Monitoreo con CloudWatch



\### Logs



Cada servicio ECS envia logs a CloudWatch:



aws logs create-log-group --log-group-name /ecs/banco-xyz/cuenta-service



\### Alarmas



\- CPU alta (> 80% por 5 min)

\- Memoria alta (> 80% por 5 min)

\- Errores 5xx (> 10 por min)

\- Target unhealthy



\---



\## 10. Estimacion de costos



\### Costo mensual estimado



| Servicio | Costo/mes |

|---|---|

| ECS Fargate (10 tasks) | \~150 USD |

| RDS PostgreSQL | \~15 USD |

| ALB | \~25 USD |

| CloudWatch | \~10 USD |

| EC2 (Keycloak) | \~30 USD |

| TOTAL | \~230 USD |



\### Optimizaciones



\- Usar Fargate Spot (hasta 70% descuento)

\- Reservar instancias RDS

\- Desactivar auto-scaling en horarios sin uso



\---



\## Checklist de despliegue



\- \[ ] Cuenta AWS configurada

\- \[ ] AWS CLI instalada

\- \[ ] Imagenes Docker subidas a ECR

\- \[ ] RDS PostgreSQL creada

\- \[ ] Keycloak desplegado

\- \[ ] Eureka Server desplegado

\- \[ ] Config Server desplegado

\- \[ ] 4 microservicios desplegados

\- \[ ] 3 BFF desplegados

\- \[ ] ALB configurado

\- \[ ] Auto-scaling configurado

\- \[ ] CloudWatch logs y alarmas

\- \[ ] DNS en Route 53

\- \[ ] Certificado SSL (ACM)



\---



Autor: Carolina Solis

Repositorio: https://github.com/carosolis45/banco-xyz-microservicioss


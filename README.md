# Pedidos360 – Productos

Microservicio de productos (Spring Boot 4.1, Java 17) protegido con JWT de Amazon Cognito.
Forma parte de Pedidos360 junto con `Pedidos360-backend` (microservicio de pedidos).

| Puerto | Endpoints |
|---|---|
| 8082 | `GET/POST /api/productos`, `GET/DELETE /api/productos/{id}`, `GET /actuator/health` |

## Autorización

| Endpoint | Requisito del token |
|---|---|
| `GET /api/productos`, `GET /api/productos/{id}` | scope `rs-api-productos/productos-read` |
| `POST /api/productos` | scope `rs-api-productos/productos-write` |
| `DELETE /api/productos/{id}` | grupo Cognito `admin` (rol `ROLE_ADMIN`) |
| `/actuator/health` | público |

Valida firma (JWKS del user pool), vigencia, `iss`, `token_use = access` y `client_id`.
Sin token o token inválido responde **401**; token válido sin permisos responde **403** (ambos en JSON).

## Ejecutar en local (BD en memoria H2)

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

## Ejecutar contra RDS (MySQL)

Cada microservicio usa su propia base de datos (esquema `productos`). Credenciales por variables de entorno:

```bash
export DB_URL="jdbc:mysql://<endpoint-rds>:3306/productos"
export DB_USER="..."
export DB_PASSWORD="..."
java -jar target/pedidos360-productos.jar
```

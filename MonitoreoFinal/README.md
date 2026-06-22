# Microservicio de Monitoreo

Java 17 · Spring Boot 3.1.12 · JWT · Swagger/OpenAPI · MySQL (Laragon) · 18 pruebas unitarias

---

## 1. Base de datos (Laragon)

1. Abre **HeidiSQL** o **phpMyAdmin** desde Laragon.
2. Ejecuta el script `db_monitoreo.sql` (incluido en este ZIP).
   - Crea la base `db_monitoreo`
   - Crea la tabla `alertas`
   - Inserta 5 registros de prueba

## 2. Levantar el proyecto

```bash
cd MonitoreoFinal
mvnw spring-boot:run
```

Verifica que levantó en: **http://localhost:8084**

## 3. Swagger UI

http://localhost:8084/swagger-ui.html

1. Clic en **Authorize**
2. Pega: `Bearer {tu_token}` (obtenido del login)

## 4. Pruebas unitarias

```bash
mvnw test
```

18 tests cubriendo: servicio (CRUD), controladores (con/sin token, validaciones), y generación/validación de JWT. Corren con base de datos H2 en memoria, no requieren MySQL.

---

## 5. Probar con Postman

### Paso 1 — Login
```
POST http://localhost:8084/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```
Copia el campo `token` de la respuesta.

### Paso 2 — Autorizar
En cada request siguiente: pestaña **Authorization** → Type **Bearer Token** → pega el token.

### Paso 3 — Endpoints

| Método | URL | Body |
|---|---|---|
| GET | `http://localhost:8084/api/monitoreo/alerta` | — |
| POST | `http://localhost:8084/api/monitoreo/alerta` | `{"tipoAlerta":"CRITICA","descripcion":"Falla red","estado":"ACTIVA"}` |
| PUT | `http://localhost:8084/api/monitoreo/alerta/1` | `{"tipoAlerta":"INFO","descripcion":"Resuelto","estado":"RESUELTA"}` |
| DELETE | `http://localhost:8084/api/monitoreo/alerta/1` | — |

**Valores válidos:**
- `tipoAlerta`: `CRITICA` \| `ADVERTENCIA` \| `INFO`
- `estado`: `ACTIVA` \| `RESUELTA` \| `PENDIENTE`

---

## Estructura del proyecto

```
MonitoreoFinal/
├── pom.xml
├── db_monitoreo.sql
├── README.md
└── src/
    ├── main/
    │   ├── java/proyecto/Monitoreo/
    │   │   ├── MonitoreoApplication.java
    │   │   ├── config/         (SecurityConfig, SwaggerConfig)
    │   │   ├── controller/     (AuthController, MonitoreoController)
    │   │   ├── exception/      (GlobalExceptionHandler)
    │   │   ├── modelo/         (ModeloAlerta)
    │   │   ├── repository/     (AlertaRepository)
    │   │   ├── security/       (JwtUtil, JwtFilter, dto/)
    │   │   └── service/        (MonitoreoService)
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/proyecto/Monitoreo/
        │   ├── MonitoreoApplicationTests.java
        │   ├── controller/     (AuthControllerTest, MonitoreoControllerTest)
        │   ├── security/       (JwtUtilTest)
        │   └── service/        (MonitoreoServiceTest)
        └── resources/
            └── application-test.properties
```

# Guía de pruebas de los microservicios

Esta guía asume que ya levantaste todo con `docker compose up -d` y que los servicios responden en sus puertos.

Puedes probar de dos formas:
- **Directo** a cada servicio (`localhost:808x`).
- **A través del Gateway** (`localhost:8090`), usando los prefijos `/api/...`.

Los ejemplos usan `curl`. En Windows PowerShell, reemplaza los saltos de línea `\` por backtick `` ` `` o escribe el comando en una sola línea.

---

## 0. Verificar que todo está arriba

```bash
curl http://localhost:8080/api-docs        # Geolocalizacion
curl http://localhost:8081/api-docs        # Prediccion
curl http://localhost:8082/v3/api-docs     # Horario
curl http://localhost:8083/v3/api-docs     # Ruta
curl http://localhost:8084/v3/api-docs     # Monitoreo
curl http://localhost:8090/actuator/health # Gateway
```

Swagger UI en el navegador:
- Geolocalización: http://localhost:8080/swagger-ui.html
- Predicción: http://localhost:8081/swagger-ui.html
- Horario: http://localhost:8082/swagger-ui.html
- Ruta: http://localhost:8083/swagger-ui.html
- Monitoreo: http://localhost:8084/swagger-ui.html
- Gateway (agrupado): http://localhost:8090/swagger-ui.html

---

## 1. Geolocalización (puerto 8080) — base `db_transporte`

Crear ubicación:
```bash
curl -X POST http://localhost:8080/api/ubicaciones \
  -H "Content-Type: application/json" \
  -d '{"unidadTransporte":"BUS-001","latitud":-33.4489,"longitud":-70.6693,"fechaHora":"2024-01-15T10:30:00"}'
```

Listar:
```bash
curl http://localhost:8080/api/ubicaciones
```

Actualizar (id 1):
```bash
curl -X PUT http://localhost:8080/api/ubicaciones/1 \
  -H "Content-Type: application/json" \
  -d '{"unidadTransporte":"BUS-999","latitud":-34.0,"longitud":-71.0}'
```

A través del Gateway: cambia `http://localhost:8080/api/ubicaciones` por `http://localhost:8090/api/ubicaciones`.

---

## 2. Predicción (puerto 8081) — base `db_prediccion`

Al crear una predicción, el servicio **automáticamente envía una ubicación al microservicio de Geolocalización** (prueba de integración 1).

```bash
curl -X POST http://localhost:8081/api/predicciones \
  -H "Content-Type: application/json" \
  -d '{"unidadTransporte":"BUS-001","latitudPredicha":-33.4489,"longitudPredicha":-70.6693,"tiempoEstimadoLlegada":"2024-01-15T12:00:00","observacion":"Trafico moderado"}'
```

Listar predicciones:
```bash
curl http://localhost:8081/api/predicciones
```

Verifica la integración: tras el POST anterior, lista las ubicaciones y deberías ver una nueva creada por Predicción:
```bash
curl http://localhost:8080/api/ubicaciones
```

A través del Gateway: `http://localhost:8090/api/predicciones`.

---

## 3. Horario (puerto 8082) — base `db_horario`

Crear horario:
```bash
curl -X POST http://localhost:8082/api/horarios \
  -H "Content-Type: application/json" \
  -d '{"ruta":"Ruta Centro","horaSalida":"08:30:00","estado":"PROGRAMADO","ajusteMinutos":0}'
```

Listar:
```bash
curl http://localhost:8082/api/horarios
```

Actualizar (id 1):
```bash
curl -X PUT http://localhost:8082/api/horarios/1 \
  -H "Content-Type: application/json" \
  -d '{"ruta":"Ruta Centro","horaSalida":"08:30:00","estado":"RETRASADO","ajusteMinutos":15}'
```

Eliminar (id 1):
```bash
curl -X DELETE http://localhost:8082/api/horarios/1
```

A través del Gateway: `http://localhost:8090/api/horarios`.

---

## 4. Ruta (puerto 8083) — base `db_ruta`

El microservicio Ruta no tiene endpoint de creación; consulta y ajusta rutas existentes. Primero **inserta algunas rutas de prueba** (la tabla la crea automáticamente al arrancar):

```bash
docker exec -i ms-mysql mysql -uroot db_ruta -e \
"INSERT INTO rutas (nombre_ruta, origen, destino, estado, tiempo_estimado) VALUES \
('Ruta Centro','Plaza de Armas','Terminal Sur','PROGRAMADA',30), \
('Ruta Norte','Estacion Central','Quilicura','PROGRAMADA',45);"
```

Listar rutas:
```bash
curl http://localhost:8083/api/rutas
```

Consultar horarios en vivo desde el microservicio Horario (prueba de integración 2):
```bash
curl http://localhost:8083/api/rutas/horarios
```

Actualizar estado de rutas (las marca AJUSTADA y suma 15 min):
```bash
curl -X PUT http://localhost:8083/api/rutas/actualizar
```

A través del Gateway: `http://localhost:8090/api/rutas`.

---

## 5. Monitoreo (puerto 8084, protegido con JWT) — base `db_monitoreo`

### Paso 1 — Login para obtener el token

```bash
curl -X POST http://localhost:8084/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Copia el valor del campo `token` de la respuesta.

### Paso 2 — Usar el token (reemplaza TU_TOKEN)

Crear alerta:
```bash
curl -X POST http://localhost:8084/api/monitoreo/alerta \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN" \
  -d '{"tipoAlerta":"CRITICA","descripcion":"Falla en la red","estado":"ACTIVA"}'
```

Listar alertas:
```bash
curl http://localhost:8084/api/monitoreo/alerta \
  -H "Authorization: Bearer TU_TOKEN"
```

Actualizar (id 1):
```bash
curl -X PUT http://localhost:8084/api/monitoreo/alerta/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN" \
  -d '{"tipoAlerta":"INFO","descripcion":"Resuelto","estado":"RESUELTA"}'
```

Eliminar (id 1):
```bash
curl -X DELETE http://localhost:8084/api/monitoreo/alerta/1 \
  -H "Authorization: Bearer TU_TOKEN"
```

Valores válidos:
- `tipoAlerta`: `CRITICA` | `ADVERTENCIA` | `INFO`
- `estado`: `ACTIVA` | `RESUELTA` | `PENDIENTE`

Prueba de seguridad: sin el header `Authorization` debes recibir 401/403.
```bash
curl -i http://localhost:8084/api/monitoreo/alerta
```

A través del Gateway: usa `http://localhost:8090/api/auth/login` para el login y `http://localhost:8090/api/monitoreo/alerta` para el resto (con el mismo header Bearer).

---

## 6. Pruebas de integración entre microservicios (resumen)

1. **Predicción → Geolocalización**: crear una predicción (sección 2) genera una ubicación nueva. Verifícalo listando ubicaciones.
2. **Ruta → Horario**: `GET /api/rutas/horarios` (sección 4) trae datos en vivo desde el servicio Horario.
3. **Monitoreo → Horario**: al crear una alerta en Monitoreo, este notifica al endpoint interno `POST /api/horarios/actualizar` del servicio Horario, que reajusta los horarios. Verifícalo creando una alerta y luego listando horarios.

---

## 7. Pruebas unitarias (sin Docker)

Cada proyecto trae sus tests con base de datos H2 en memoria. Para correrlos localmente:

```bash
cd MonitoreoFinal && ./mvnw test     # 18 tests
cd Gateway && ./mvnw test
# ...y así con cada microservicio
```

---

## 8. Diagnóstico rápido

```bash
docker compose ps                 # estado de cada contenedor
docker compose logs -f gateway    # logs de un servicio
docker compose logs -f mysql
docker exec -it ms-mysql mysql -uroot -e "SHOW DATABASES;"   # ver las 5 bases creadas
```

Si un microservicio no conecta a la base: revisa que `mysql` aparezca `healthy` en `docker compose ps` antes de que arranquen los servicios (el `docker-compose.yml` ya espera por eso).

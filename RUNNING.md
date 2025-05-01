# Ejecutando la API Challenger

## Requisitos Previos

- Docker y Docker Compose
- No se requiere Java instalado localmente

## Pasos para Ejecutar la Aplicación

### 1. Iniciar con Docker Compose

La forma más sencilla de ejecutar la aplicación es usando Docker Compose:

```bash
docker-compose up
```

Este comando:
1. Construirá la aplicación Spring Boot dentro de un contenedor Docker
2. Iniciará la base de datos PostgreSQL en un contenedor
3. Conectará la aplicación a la base de datos
4. Expondrá la aplicación en el puerto 8080

La primera construcción puede tardar unos minutos ya que necesita descargar dependencias.

### 2. Verificar que la Aplicación está Funcionando

Una vez que la aplicación esté en funcionamiento, deberías ver mensajes en la consola indicando que la aplicación ha iniciado correctamente. Puedes verificar que los contenedores están en ejecución con:

```bash
docker ps
```

Deberías ver dos contenedores: `challenger-app` y `challenger-db`.

### 3. Acceder a la Documentación de la API

Abre un navegador y accede a:

```
http://localhost:8080/swagger-ui.html
```

Esto te mostrará la interfaz de Swagger donde puedes probar todos los endpoints de la API.

### 4. Probar la API

#### Calcular con porcentaje dinámico

```bash
curl -X POST http://localhost:8080/api/calculations \
  -H "Content-Type: application/json" \
  -d '{"num1": 10.0, "num2": 20.0}'
```

#### Obtener historial de llamadas (con paginación)

```bash
# Historial básico con paginación
curl -X GET "http://localhost:8080/api/history?page=0&size=10"

# Filtrar por endpoint
curl -X GET "http://localhost:8080/api/history?endpoint=/api/calculations"

# Filtrar por fechas (formato simple YYYY-MM-DD)
curl -X GET "http://localhost:8080/api/history?startDate=2025-04-29&endDate=2025-04-29"
```

#### Obtener historial simplificado (sin paginación)

```bash
# Historial simplificado (últimas 5 entradas)
curl -X GET "http://localhost:8080/api/history/simple?limit=5"

# Historial simplificado filtrado por endpoint
curl -X GET "http://localhost:8080/api/history/simple?endpoint=/api/calculations"
```

### 5. Detener la Aplicación

Para detener la aplicación:

```bash
docker-compose down
```

Para detener y eliminar los volúmenes (esto eliminará los datos de la base de datos):

```bash
docker-compose down -v
```

## Solución de Problemas

Si encuentras algún problema al ejecutar la aplicación, consulta la sección "Solución de Problemas" en el archivo README.md.

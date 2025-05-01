# Documentación de la API

## Introducción

Esta API proporciona dos funcionalidades principales:
1. Cálculo con porcentaje dinámico
2. Historial de llamadas a la API

## Endpoints

### 1. API de Cálculo

#### Realizar un cálculo

```
POST /api/calculations
```

**Descripción**: Suma dos números y aplica un porcentaje dinámico obtenido de un servicio externo (simulado).

**Cuerpo de la solicitud**:
```json
{
  "num1": 10.0,
  "num2": 20.0
}
```

**Respuesta exitosa** (200 OK):
```json
{
  "num1": 10.0,
  "num2": 20.0,
  "sum": 30.0,
  "percentage": 10.0,
  "result": 33.0,
  "timestamp": "2025-04-29 12:34:56"
}
```

**Respuesta de error** (503 Service Unavailable):
```json
{
  "timestamp": "2025-04-29T12:34:56.789",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Error al obtener el porcentaje del servicio externo (FALLO SIMULADO A PROPÓSITO - Parte del diseño)"
}
```

> **Nota**: Este error 503 es parte del diseño de la aplicación. El servicio externo está programado para fallar aleatoriamente con una probabilidad del 10% para simular condiciones del mundo real. Si ya has realizado cálculos exitosos anteriormente, el sistema usará el último porcentaje almacenado en caché.

**Respuesta de error por límite de tasa** (429 Too Many Requests):
```json
{
  "timestamp": "2025-04-29T12:34:56.789",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Has excedido el límite de solicitudes. Por favor, intenta de nuevo más tarde."
}
```

> **Nota**: Este endpoint tiene un límite de 5 solicitudes por ventana de 10 segundos. Si excedes este límite, recibirás un error 429. Después de 10 segundos, el contador se reiniciará.

### 2. API de Historial de Llamadas

#### Obtener historial con paginación

```
GET /api/history
```

**Descripción**: Devuelve el historial de llamadas a la API con paginación y filtros opcionales.

**Parámetros de consulta**:
- `endpoint` (opcional): Filtrar por endpoint (ejemplo: `/api/calculations`)
- `startDate` (opcional): Filtrar por fecha de inicio (formato: YYYY-MM-DD)
- `endDate` (opcional): Filtrar por fecha de fin (formato: YYYY-MM-DD)
- `page` (opcional): Número de página (por defecto: 0)
- `size` (opcional): Tamaño de página (por defecto: 10)

**Respuesta exitosa** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "endpoint": "/api/calculations",
      "timestamp": "2025-04-29T12:34:56.789",
      "parameters": "{\"body\":\"{\\\"num1\\\":10.0,\\\"num2\\\":20.0}\",\"method\":\"POST\"}",
      "response": "{\"num1\":10.0,\"num2\":20.0,\"sum\":30.0,\"percentage\":10.0,\"result\":33.0,\"timestamp\":\"2025-04-29 12:34:56\"}",
      "errorMessage": null,
      "successful": true
    },
    // ... más entradas
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 1,
  "size": 10,
  "number": 0,
  "empty": false
}
```

#### Obtener historial simplificado (sin paginación)

```
GET /api/history/simple
```

**Descripción**: Versión simplificada que devuelve una lista en lugar de una página paginada.

**Parámetros de consulta**:
- `endpoint` (opcional): Filtrar por endpoint (ejemplo: `/api/calculations`)
- `limit` (opcional): Número máximo de resultados (por defecto: 10)

**Respuesta exitosa** (200 OK):
```json
[
  {
    "id": 1,
    "endpoint": "/api/calculations",
    "timestamp": "2025-04-29T12:34:56.789",
    "parameters": "{\"body\":\"{\\\"num1\\\":10.0,\\\"num2\\\":20.0}\",\"method\":\"POST\"}",
    "response": "{\"num1\":10.0,\"num2\":20.0,\"sum\":30.0,\"percentage\":10.0,\"result\":33.0,\"timestamp\":\"2025-04-29 12:34:56\"}",
    "errorMessage": null,
    "successful": true
  },
  // ... más entradas (hasta el límite especificado)
]
```

## Ejemplos de uso

### Cálculo con porcentaje dinámico

```bash
curl -X POST http://localhost:8080/api/calculations \
  -H "Content-Type: application/json" \
  -d '{"num1": 10.0, "num2": 20.0}'
```

### Obtener historial con paginación

```bash
# Historial básico con paginación
curl -X GET "http://localhost:8080/api/history?page=0&size=10"

# Filtrar por endpoint
curl -X GET "http://localhost:8080/api/history?endpoint=/api/calculations"

# Filtrar por fechas
curl -X GET "http://localhost:8080/api/history?startDate=2025-04-29&endDate=2025-04-29"
```

### Obtener historial simplificado

```bash
# Historial simplificado (últimas 5 entradas)
curl -X GET "http://localhost:8080/api/history/simple?limit=5"

# Historial simplificado filtrado por endpoint
curl -X GET "http://localhost:8080/api/history/simple?endpoint=/api/calculations"
```

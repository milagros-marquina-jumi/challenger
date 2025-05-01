# Challenger API

Una API REST de Spring Boot que realiza cálculos con porcentajes dinámicos y mantiene un historial de llamadas.

## Características

- **Cálculo dinámico de porcentajes**: Suma dos números y aplica un porcentaje obtenido de un servicio externo
- **Almacenamiento en caché de porcentajes**: Almacena el porcentaje en caché durante 30 minutos para mejorar el rendimiento
- **Registro de llamadas asíncrono**: Registra todas las llamadas a la API sin afectar el rendimiento
- **Límite de velocidad**: Evita el abuso al limitar el número de solicitudes
- **Documentación de Swagger**: Documentación interactiva de la API
- **Compatibilidad con Docker**: Implementación sencilla con Docker Compose

## Tecnologías

- Java 21
- Spring Boot 3.4.5
- Spring Data JPA
- PostgreSQL
- Lombok
- Caffeine Cache
- Resilience4j (Rate Limiting)
- Swagger/OpenAPI
- Docker & Docker Compose
- JUnit 5 & Mockito

## Requisitos

- Docker and Docker Compose (no se requiere Java instalado localmente)

## Ejecutar la aplicación

### Usando Docker Compose

La forma más fácil de ejecutar la aplicación es usando Docker Compose:

```bash
# En Windows
docker-compose up

# En Linux/Mac (o si estás usando Git Bash en Windows)
chmod +x run.sh
./run.sh
```

Esto hará:
1. Construir la aplicación Spring Boot dentro de un contenedor Docker
2. Iniciar la base de datos PostgreSQL en un contenedor
3. Conectar la aplicación a la base de datos
4. Exponer la aplicación en el puerto 8080

La primera construcción puede tardar unos minutos ya que necesita descargar dependencias.

> **Nota**: No necesitas tener Java instalado en tu sistema. Todo se ejecuta dentro de los contenedores Docker.

## Documentación de la API

### Swagger UI

Una vez que la aplicación esté en funcionamiento, puedes acceder a la interfaz de Swagger en:

```
http://localhost:8080/swagger-ui.html
```

Esta interfaz te permitirá probar todos los endpoints de la API directamente desde el navegador.

### Documentación Detallada

Para una documentación más detallada de la API, consulta el archivo [API.md](API.md) que incluye:

- Descripción completa de todos los endpoints
- Formato de las solicitudes y respuestas
- Ejemplos de uso con curl
- Manejo de errores

## Endpoints de la API

### API de Cálculo

- **POST /api/calculations**
  - Suma dos números y aplica un porcentaje dinámico
  - Cuerpo de la solicitud: `{ "num1": 10.0, "num2": 20.0 }`
  - Respuesta: `{ "num1": 10.0, "num2": 20.0, "sum": 30.0, "percentage": 10.0, "result": 33.0, "timestamp": "2025-04-29 12:34:56" }`
  - Ejemplo con curl:
    ```bash
    curl -X POST http://localhost:8080/api/calculations \
      -H "Content-Type: application/json" \
      -d '{"num1": 10.0, "num2": 20.0}'
    ```

### API de Historial de Llamadas

#### Endpoint principal con paginación

- **GET /api/history**
  - Devuelve el historial de llamadas a la API con paginación
  - Parámetros de consulta simplificados:
    - `endpoint`: Filtrar por endpoint (opcional)
    - `startDate`: Filtrar por fecha de inicio (formato: YYYY-MM-DD) (opcional)
    - `endDate`: Filtrar por fecha de fin (formato: YYYY-MM-DD) (opcional)
    - `page`: Número de página (por defecto: 0)
    - `size`: Tamaño de página (por defecto: 10)
  - Ejemplo con curl:
    ```bash
    curl -X GET "http://localhost:8080/api/history?page=0&size=10"
    ```
  - Ejemplo con filtro por endpoint:
    ```bash
    curl -X GET "http://localhost:8080/api/history?endpoint=/api/calculations"
    ```
  - Ejemplo con filtro por fechas:
    ```bash
    curl -X GET "http://localhost:8080/api/history?startDate=2025-04-29&endDate=2025-04-29"
    ```

#### Endpoint simplificado (sin paginación)

- **GET /api/history/simple**
  - Versión simplificada que devuelve una lista en lugar de una página paginada
  - Parámetros de consulta:
    - `endpoint`: Filtrar por endpoint (opcional)
    - `limit`: Número máximo de resultados (por defecto: 10)
  - Ejemplo con curl:
    ```bash
    curl -X GET "http://localhost:8080/api/history/simple?limit=5"
    ```
  - Ejemplo con filtro por endpoint:
    ```bash
    curl -X GET "http://localhost:8080/api/history/simple?endpoint=/api/calculations&limit=5"
    ```

## Arquitectura

La aplicación sigue una arquitectura en capas:

- **Capa de Controlador**: Maneja las peticiones y respuestas HTTP
- **Capa de Servicio**: Contiene la lógica de negocio
- **Capa de Repositorio**: Interactúa con la base de datos
- **Capa de Filtro**: Registra las llamadas a la API de forma asíncrona

### Características Técnicas Implementadas

1. **Cálculo con Porcentaje Dinámico**:
   - Endpoint que recibe dos números, los suma y aplica un porcentaje
   - El porcentaje se obtiene de un servicio externo simulado
   - Uso de tipos primitivos (double) para mayor eficiencia
   - Simulación de fallos (error 503) con un 10% de probabilidad para demostrar resiliencia

2. **Caché del Porcentaje**:
   - El porcentaje se almacena en caché durante 30 minutos
   - Si el servicio externo falla, se usa el último valor almacenado

3. **Historial de Llamadas**:
   - Registro asíncrono de todas las llamadas a la API
   - Almacenamiento en PostgreSQL
   - Dos endpoints para consultar el historial:
     - `/api/history`: Con paginación y filtros por endpoint y fechas (formato simple YYYY-MM-DD)
     - `/api/history/simple`: Versión simplificada sin paginación, ideal para pruebas rápidas

4. **Limitación de Tasa (Rate Limiting)**:
   - Implementación directa con AtomicInteger y AtomicLong para mayor fiabilidad
   - Límite de 5 solicitudes por ventana de 10 segundos para el endpoint de cálculos
   - Respuesta de error 429 (Too Many Requests) cuando se excede el límite
   - Fácilmente ajustable modificando las constantes en el controlador

5. **Características Adicionales**:
   - Uso de Java Records para modelos de petición/respuesta
   - Configuración de pool de hilos para operaciones asíncronas
   - Manejo de errores con @ExceptionHandler
   - Implementación manual de caché para el porcentaje
   - Documentación detallada en README.md, RUNNING.md y API.md
   - Formato de fecha simplificado para facilitar las pruebas
   - Parámetros de consulta intuitivos para la API de historial
   - Comentarios en español para facilitar el mantenimiento

## Pruebas

Las pruebas se ejecutan automáticamente durante la construcción de la imagen Docker. Si deseas ejecutar las pruebas manualmente (requiere Java 21 instalado):

```bash
./gradlew test
```

O dentro del contenedor Docker:

```bash
docker-compose exec app java -jar /app.jar --test
```

## Solución de Problemas

### Problemas con la Construcción de Docker

Si encuentras problemas con la construcción de Docker:

1. Asegúrate de que Docker y Docker Compose estén instalados y en ejecución
2. Intenta reconstruir las imágenes:
   ```bash
   docker-compose down
   docker-compose build --no-cache
   docker-compose up
   ```

3. Verifica los logs para errores específicos:
   ```bash
   docker-compose logs app
   ```

### Problemas de Conexión a la Base de Datos

Si la aplicación no puede conectarse a la base de datos:

1. Asegúrate de que el contenedor de PostgreSQL esté en ejecución:
   ```bash
   docker ps
   ```

2. Verifica los logs de PostgreSQL:
   ```bash
   docker-compose logs db
   ```

### Problemas Comunes

1. **Error "port is already allocated"**: Otro servicio está usando el puerto 8080 o 5432
   ```bash
   # Solución: Cambia los puertos en docker-compose.yml
   ports:
     - "8081:8080"  # Cambia 8080 por 8081
   ```

2. **Error "connection refused"**: La base de datos aún no está lista
   ```bash
   # Solución: Espera unos segundos y vuelve a intentarlo
   # La aplicación tiene un mecanismo de reintento incorporado
   ```

## Mejoras Futuras

- Añadir autenticación y autorización
- Implementar estrategias de caché más sofisticadas
- Añadir métricas y monitorización
- Implementar circuit breaker para llamadas a servicios externos
- Añadir más pruebas de integración
- Implementar CI/CD para despliegue automático

# To-Do List API

API REST para gestión de tareas y categorías, construida con Spring Boot 4 y Java 25. Permite crear, consultar, actualizar y eliminar tareas organizadas por categorías, con persistencia en PostgreSQL.

---

## Tecnologías y versiones

| Tecnología      | Versión                 |
|-----------------|-------------------------|
| Java            | 25                      |
| Spring Boot     | 4.0.6                   |
| Spring Data JPA | (incluido en Boot)      |
| Spring MVC      | (incluido en Boot)      |
| Hibernate       | (incluido en Boot)      |
| PostgreSQL      | 17.9                    |
| MapStruct       | 1.6.3                   |
| Lombok          | 1.18.46                 |
| Maven           | 3.9.15                  |

---

## Arquitectura

El proyecto sigue el patrón **MVC en capas**:

```
Controller  →  Service (interfaz + impl)  →  Repository  →  Base de datos
                    ↕
                  Mapper (MapStruct)
                    ↕
                   DTO / Entity
```

| Capa       | Paquete                      | Responsabilidad                                      |
|------------|------------------------------|------------------------------------------------------|
| Controller | `controller/`                | Recibe peticiones HTTP y devuelve respuestas JSON    |
| Service    | `service/` + `service/impl/` | Lógica de negocio con interfaces desacopladas        |
| Repository | `repository/`                | Acceso a datos mediante Spring Data JPA              |
| Model      | `model/`                     | Entidades JPA mapeadas a tablas PostgreSQL           |
| DTO        | `dto/`                       | Objetos de transferencia de datos (Java Records)     |
| Mapper     | `mapper/`                    | Conversión Entity ↔ DTO con MapStruct               |
| Exception  | `exception/`                 | Manejo global de errores con `@RestControllerAdvice` |
| Config     | `config/`                    | Propiedades tipadas con `@ConfigurationProperties`   |

---

## Modelo de datos

### Categoría (`categorias`)

| Campo    | Tipo   | Restricciones       |
|----------|--------|---------------------|
| `id`     | Long   | PK, autoincremental |
| `nombre` | String | NOT NULL, UNIQUE    |

### Tarea (`tareas`)

| Campo           | Tipo          | Restricciones                    |
|-----------------|---------------|----------------------------------|
| `id`            | Long          | PK, autoincremental              |
| `titulo`        | String        | NOT NULL, max 150 caracteres     |
| `categoria_id`  | Long          | FK → `categorias`, NOT NULL      |
| `completada`    | boolean       | Estado de la tarea               |
| `fecha_creacion`| LocalDateTime | Solo inserción (no actualizable) |

**Relación:** `ManyToOne` — una categoría puede tener múltiples tareas.

---

## Endpoints

### Categorias — `/api/categorias`

| Método   | URL                    | Descripción                      | Body requerido          |
|----------|------------------------|----------------------------------|-------------------------|
| `POST`   | `/api/categorias`      | Crea una nueva categoría         | `{"nombre": "Trabajo"}` |
| `GET`    | `/api/categorias`      | Lista todas las categorías       | —                       |
| `PUT`    | `/api/categorias/{id}` | Actualiza el nombre de categoría | `{"nombre": "Hogar"}`   |
| `DELETE` | `/api/categorias/{id}` | Elimina una categoría            | —                       |

> **Regla de negocio:** No se puede actualizar ni eliminar una categoría que tenga tareas asociadas.

---

### Tareas — `/api/tareas`

| Método   | URL                              | Descripción                           |
|----------|----------------------------------|---------------------------------------|
| `POST`   | `/api/tareas`                    | Crea una nueva tarea                  |
| `GET`    | `/api/tareas`                    | Lista todas las tareas                |
| `GET`    | `/api/tareas?categoria=Trabajo`  | Lista tareas filtradas por categoría  |
| `PUT`    | `/api/tareas/{id}`               | Actualiza título y categoría de tarea |
| `DELETE` | `/api/tareas/{id}`               | Elimina una tarea                     |

**Ejemplos de body:**

```json
// POST /api/tareas  →  201 Created
// Request
{
    "titulo": "Preparar informe mensual",
    "nombreCategoria": "Trabajo"
}
// Response
{
    "id": 1,
    "titulo": "Preparar informe mensual",
    "nombreCategoria": "Trabajo",
    "completada": false
}
```

```json
// PUT /api/tareas/1  →  200 OK
{
    "titulo": "Preparar informe semanal",
    "nombreCategoria": "Trabajo"
}
```

---

## Manejo de errores

Todos los errores devuelven un formato estructurado gracias a `GlobalExceptionHandler` (`@RestControllerAdvice`):

```json
{
    "timestamp": "2026-05-05T10:30:00",
    "status": 404,
    "error": "Not Found",
    "message": "Categoría 'Universidad' no encontrada.",
    "path": "/api/tareas"
}
```

Casos cubiertos:
- Tarea o categoría no encontrada por ID
- Categoría duplicada al crear
- Intento de eliminar/actualizar categoría con tareas asociadas

---

## Características técnicas destacadas

### Spring Data JPA — Query Methods derivados

Los repositorios evitan SQL manual usando métodos con nombre semántico. Spring Data genera la consulta automáticamente:

```java
// ITareaRepository.java
List<TareaEntity> findByCategoriaNombreIgnoreCase(String nombre); // JOIN + case-insensitive
long countByCategoriaId(Long categoriaId);                        // COUNT con FK
```

### MapStruct — Mapeo automático Entity ↔ DTO

Convierte entre capas sin código boilerplate. El procesador genera la implementación en tiempo de compilación:

```java
@Mapper(componentModel = "spring")
public interface TareaMapper {
    @Mapping(source = "nombreCategoria", target = "categoria.nombre")
    TareaEntity toEntity(TareaDTO dto);

    @Mapping(source = "categoria.nombre", target = "nombreCategoria")
    TareaDTO toDto(TareaEntity entity);
}
```

### Java Records como DTO y configuración tipada

DTOs inmutables con Bean Validation integrado:

```java
public record TareaDTO(
    Long id,
    @NotBlank(message = "Debe ingresar un nombre de la tarea") String titulo,
    @NotBlank(message = "Debe ingresar una categoria para la tarea") String nombreCategoria,
    Boolean completada
) {}
```

Propiedades de `application.properties` enlazadas y validadas directamente como bean:

```java
@ConfigurationProperties(prefix = "app")
@Validated
public record InfoAppConfig(
    @NotBlank String developer,
    @Pattern(regexp = "^[0-9]+\\.[0-9]+\\.[0-9]+$") String version
) {}
```

### Inyección de dependencias por constructor

Todos los componentes usan inyección por constructor en lugar de `@Autowired`, siguiendo las mejores prácticas de Spring para facilitar pruebas unitarias y favorecer la inmutabilidad.

### Lombok — Reducción de boilerplate

Las entidades JPA usan `@Data` y `@NoArgsConstructor`, eliminando getters, setters y constructores manuales del código fuente.

### Dockerfile multi-etapa

La imagen Docker usa una construcción en dos etapas para minimizar el tamaño final:

```dockerfile
# Etapa 1: compilación con Maven
FROM maven:3.9.15-eclipse-temurin-25 AS build
# Etapa 2: ejecución solo con JRE
FROM eclipse-temurin:25-jre
```

---

## Ejecución

### Con Docker (recomendado)

```bash
docker-compose up --build
```

Levanta dos servicios:
- `todo-postgres` — PostgreSQL 17.9 en el puerto `5432`
- `todo-app-java` — La aplicación en el puerto `8080`

### Local (IntelliJ / Maven)

Requiere PostgreSQL corriendo localmente con base de datos `todo_db`:

```bash
./mvnw spring-boot:run
```

La aplicación detecta automáticamente si corre en Docker o local mediante la variable de entorno `DB_HOST`. Hibernate crea y actualiza las tablas automáticamente (`ddl-auto=update`).

---

## Estructura del proyecto

```
src/main/java/com/practicandospring/todolist/
├── config/
│   ├── AppConfig.java              # Habilita @ConfigurationProperties
│   └── InfoAppConfig.java          # Propiedades tipadas (app.developer, app.version)
├── controller/
│   ├── TareaController.java
│   └── CategoriaController.java
├── dto/
│   └── TareaDTO.java               # Java Record con Bean Validation
├── exception/
│   ├── ErrorResponse.java          # Record para respuestas de error estructuradas
│   └── GlobalExceptionHandler.java # @RestControllerAdvice global
├── mapper/
│   └── TareaMapper.java            # Interfaz MapStruct (implementación generada)
├── model/
│   ├── TareaEntity.java
│   └── CategoriaEntity.java
├── repository/
│   ├── ITareaRepository.java
│   └── ICategoriaRepository.java
└── service/
    ├── ITareaService.java
    ├── ICategoriaService.java
    └── impl/
        ├── TareaService.java
        └── CategoriaService.java
```

---

## Autor

**Jesus Sanchez** — v1.0.0

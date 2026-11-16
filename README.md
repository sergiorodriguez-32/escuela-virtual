# CampusHub — proyecto de Acceso a Datos (2.º DAM)

API REST para gestionar la actividad de un centro educativo: grupos, docentes, estudiantes,
actividades e inscripciones. Combina PostgreSQL (datos relacionales), MongoDB (seguimientos) y
Redis (caché).

## Autores

Proyecto realizado por:

- Cristina Moran
- Samuel Cleto
- Sergio Rodriguez

## Enunciado original

**IMPORTANTE:** Este repositorio es intencionadamente incompleto. La API tiene controllers REST y DTO,
pero la persistencia no existe todavía. Cada operación del servicio devuelve **501 NOT_IMPLEMENTED**.
Los tests funcionales deben pasar solo después de implementar las entidades, documentos,
repositorios, servicios y restricciones. **No modificar controllers ni DTO para eludir requisitos.**

## Requisitos

- JDK 21 y Maven 3.9+ (o wrapper Maven configurado por el centro).
- Docker Engine / Docker Desktop con Docker Compose v2.

## Estructura didáctica

El código parte de una arquitectura tradicional por capas:

```text
controller/       endpoints REST y tratamiento de errores HTTP
controller/dto/   contrato de entrada y salida de la API
service/          casos de uso y reglas de negocio
repository/       repositorios Spring Data que debe crear el alumno
entity/           entidades JPA que debe crear el alumno
document/         documentos MongoDB que debe crear el alumno
mapper/           conversores entre persistencia y DTO que debe crear el alumno
```

Los paquetes vacíos contienen `package-info.java` para explicar su responsabilidad sin entregar
la solución de persistencia. Se puede implementar la conversión manualmente o con MapStruct.
Lombok y MapStruct están disponibles de forma opcional; Maven ya incluye sus annotation processors
para Java 21. No es obligatorio usar ninguna de las dos herramientas.

## Arranque

```bash
mvn spring-boot:run
```

Spring Boot detecta `compose.yaml`, ejecuta automáticamente `docker compose up` antes de arrancar
y detiene los contenedores cuando finaliza la aplicación. Por tanto, no hace falta arrancar
PostgreSQL, MongoDB y Redis manualmente. Si se prefiere gestionarlos por separado:

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false
```

Consultar PostgreSQL:

```bash
docker compose exec postgres psql -U campushub -d campushub
```

Consultar MongoDB:

```bash
docker compose exec mongo mongosh campushub
```

Consultar Redis:

```bash
docker compose exec redis redis-cli
```

## Swagger y OpenAPI

Con la aplicación arrancada, la API se publica automáticamente en:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>
- OpenAPI YAML: <http://localhost:8080/v3/api-docs.yaml>

Springdoc obtiene el contrato inicial de los controllers y DTO. El alumno puede enriquecerlo con
anotaciones OpenAPI, pero no debe cambiar los endpoints ni los cuerpos fijados por las pruebas.

## Tests

```bash
mvn test                # tests unitarios con patrón *Test
mvn verify              # tests de integración *IT y comprobación de formato
mvn spotless:apply      # aplicar formato antes de verify
```

Testcontainers necesita un motor Docker funcional. Las pruebas crean bases de datos **aisladas**;
no utilizan las del `compose.yaml`, cuyo arranque automático está deshabilitado en el perfil de test.
En el arranque inicial los tests de contrato funcional deben fallar.

## Contrato público

Campos de fecha: cadenas ISO-8601 (`YYYY-MM-DD`). Los nombres y estructuras están fijados
por los `record` en `controller/dto`. Consultar `docs/api-contract.md`.

## Responsabilidades del alumno

- Elegir reparto PostgreSQL/MongoDB y documentarlo.
- Decidir justificadamente si Redis aporta valor; se entrega disponible, pero su uso no es obligatorio.
- Implementar entidades y asociaciones JPA; documentos MongoDB y repositorios.
- Reemplazar stubs de `service/CampusHubService` por lógica propia (puede refactorizarse).
- Crear mappers/converters manuales o interfaces MapStruct en `mapper`.
- Implementar restricciones reales de BD: FK, UNIQUE, NOT NULL, CHECK cuando corresponda.
- Mantener el contrato REST y producir códigos 400/404/409 adecuados.
- No confiar en validaciones previas de los DTO como sustituto de las restricciones de BD.

## Tratamiento de errores

Se entregan excepciones `ResourceNotFoundException` (404), `BusinessConflictException` (409),
`InvalidInputException` (400) y un handler de `DataIntegrityViolationException` (409).
Los alumnos deben lanzar las excepciones correctas desde sus casos de uso y garantizar la integridad.
Una excepción de integridad no debería exponer SQL interno. No todas las violaciones son conflictos:
los alumnos podrán necesitar diferenciar errores según la operación.

## Persistencia en desarrollo

`spring.jpa.hibernate.ddl-auto=update` facilita empezar con JPA. Para las restricciones que
JPA no represente adecuadamente se pueden aplicar scripts SQL. **No se entregan scripts de solución.**
En un entorno de producción convendría versionar el esquema con Flyway o Liquibase.

## Limitación importante

Estos ficheros proporcionan el contrato y la estructura, no una solución de referencia.
Por defecto algunas pruebas del repositorio están destinadas a fallar.

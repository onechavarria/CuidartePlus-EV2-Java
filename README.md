# CuidartePlus - EV2 DevOps Java

Proyecto con dos microservicios en **Java 21 y Spring Boot**, PostgreSQL y Docker Compose. No incluye frontend.

## Requisitos

- Docker Desktop
- Java 21 y Maven (para pruebas locales)

## Ejecutar con Docker

Desde la carpeta principal, copia `.env.example` a `.env`, reemplaza `JWT_SECRET` por una clave de al menos 32 caracteres y ejecuta:

```bash
docker compose up -d --build
docker compose ps
```

Comprobar los servicios:

- Usuarios: http://localhost:4444/health
- Pacientes: http://localhost:4445/health

El registro se realiza con `POST /autenticacion/registro` y el login con `POST /autenticacion/login`. El token recibido se utiliza como `Authorization: Bearer TOKEN` en las rutas de Pacientes.

Para detener:

```bash
docker compose down
```

Para reiniciar la base de datos **borrando sus datos**:

```bash
docker compose down -v
docker compose up -d --build
```

## Pruebas y cobertura

Los tests de repositorios usan H2 en memoria, sin necesitar PostgreSQL. La configuración de ejecución de cada microservicio está en `src/main/resources/application.yml`.


Desde la raíz:

```bash
cd usuarios
mvn clean verify
start target/site/jacoco/index.html

cd ../pacientes
mvn clean verify
start target/site/jacoco/index.html
```

Ver los reportes JaCoCo:

- `usuarios/target/site/jacoco/index.html`
- `pacientes/target/site/jacoco/index.html`

JaCoCo está configurado con un mínimo del 80 % de cobertura de líneas por microservicio.

## GitHub Actions

El workflow `.github/workflows/ci-cd.yml` se ejecuta al hacer push a `main` o manualmente. Ejecuta JUnit 5, JaCoCo, Snyk y un despliegue temporal con Docker Compose.

Antes de ejecutarlo, crea estos secretos en **Settings → Secrets and variables → Actions**:

- `SNYK_TOKEN`: token de tu cuenta Snyk.
- `JWT_SECRET`: clave JWT de al menos 32 caracteres.

Dependabot revisa Maven y GitHub Actions semanalmente. El archivo `.env` local no se sube a GitHub; se comparte `.env.example`.

## Servicios

**Usuarios:** `/health`, `POST /autenticacion/registro`, `POST /autenticacion/login`, `GET /usuarios/{usuarioId}`.

**Pacientes:** `/health`, `GET /pacientes`, `POST /pacientes`, `GET /pacientes/{id}`, `PUT /pacientes/{id}`, `DELETE /pacientes/{id}`.

Los endpoints pueden probarse con Postman o Thunder Client. Esta versión no incluye Swagger UI.

# ChatVault

_Chat backend written in Kotlin with Spring Boot and Mustache for HTML templates_

[![Docker Compose build](https://github.com/zxisatree/chatvault/actions/workflows/docker.yml/badge.svg)](https://github.com/zxisatree/chatvault/actions/workflows/docker.yml)
[![Gradle test on Windows](https://github.com/zxisatree/chatvault/actions/workflows/test.yml/badge.svg)](https://github.com/zxisatree/chatvault/actions/workflows/test.yml)

## Running the server

The backend server can be started by launching it as a standalone Spring Boot application. Users can also try out the server with the provided Docker image.

The following prerequisites are required for this project:

- `.env` file in root directory (see [Environment variable file](#environment-variable-files) for more info about the environment file)
- JDK 17
- Kotlin compiler
- PostgreSQL 14+
- Docker/Docker Desktop (only for [Using the Docker image](#using-the-docker-image))

### Using the Docker image

Run `docker compose up` in the root folder. The server will be exposed on port 1018, Prometheus on port 1029 and Grafana on port 1040.

### Without Docker

Create the database (with the user in the [`.env` file](#environment-variable-files) as the creator), and run `docker/docker-entrypoint-initdb.d/init.sql` to initialise the required tables. Then run `./gradlew bootRun` in the root directory to start the server on port 8080.

## Environment variable files

The `.env` and `env.properties` files contain secrets, and thus are not committed by default and ignored in `.gitignore`. The building of the JAR file requires `env.properties`, so it is required to be in the root directory at the time of running the Docker build command. On the other hand, `.env` is only required at runtime.

Both files should be created in the root directory. The files require the following keys (replace `{items}` in curly braces with your own values):

`env.properties`:

- `JDBC_PSQL_URI=jdbc:postgresql://{host}:{postgres_port}/{postgres_db_name}`
- `PSQL_USER={postgres_username}`
- `PSQL_PASSWORD={postgres_password}`

`.env`:

- `SPRING_DATASOURCE_URL=jdbc:postgresql://{docker_service_name}:{docker_postgres_port}/{docker_postgres_db_name}`
- `POSTGRES_DB={docker_postgres_db_name}`
- `POSTGRES_USER={docker_postgres_username}`
- `POSTGRES_PASSWORD={docker_postgres_password}`
- `GRAFANA_PASSWORD={grafana_default_password}`

`SPRING_DATASOURCE_URL` overwrites `spring.datasource.url` in `application.properties` that was set to `${JDBC_PSQL_URI}` at image build time. When using Docker, PostgreSQL will not be running on the default `localhost`, and this key points the JDBC connection string to the correct container host `docker_psql_db` instead.

`POSTGRES_DB`, `POSTGRES_USER` and `POSTGRES_PASSWORD` affect the database and user created by the PostgreSQL image.

## Project structure

`/src/main/kotlin/com.example.blog/`:

- `Advices.kt`: intercepts exceptions thrown by the application and sends HTTP responses back to the client
- `BlogApplication.kt`: main entry point
- `BlogConfiguration.kt`: configuration for the app
- `BlogProperties.kt`: configuration properties for the app
- `Entities.kt`: JPA representations of the tables in the database
- `Exceptions.kt`: custom exceptions
- `Extensions.kt`: utility functions
- `HtmlController.kt`: controller for rendering the main blog page using Mustache templates
- `HttpControllers.kt`: API controller
- `Repositories.kt`: Spring JPA repositories

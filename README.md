# Kotlin Blog
_A CRUD API to practice DevOps_

[![Docker Compose](https://github.com/zxisatree/kotlin-blog/actions/workflows/docker.yml/badge.svg)](https://github.com/zxisatree/kotlin-blog/actions/workflows/docker.yml)
[![Gradle test on Windows](https://github.com/zxisatree/kotlin-blog/actions/workflows/windows-test.yml/badge.svg)](https://github.com/zxisatree/kotlin-blog/actions/workflows/windows-test.yml)

CRUD blog backend written with Spring Boot and Mustache for HTML templates. A temporary PostgreSQL database is included in the docker image.

## Why?
The aim of this project is to learn more about Spring Boot and CI/CD. Thus, the project is intentionally kept small and simple.

This repository can be used to resolve potential issues that might be found when using Spring Boot with Kotlin, which is not as common as Java and thus harder to find answers for.

## Building the application from scratch
The following prerequisites are required for this project:
* JDK 17
* Kotlin compiler

### Building the Docker image
To build and run the Docker image, you will need Docker (or Docker Desktop for Windows).

Clone the repository, then create a `.env` file in the root directory (see [Environment variable file](#Environment-variable-file) for more info about the environment file).

Then run `docker compose up` in the root folder. The JAR file will be built and copied to the docker image. The server is exposed on port 1018 by default.

### Building without Docker
To build the application without Docker, you will also need PostgreSQL (version 14.5+) running on your system.

Run `./gradlew build -x test` in the root directory. The JAR file can be found in `/build/libs/` after building.

## Environment variable file (`.env`, `env.properties`)
The `.env` and `env.properties` files contain secrets, and thus are not committed by default and ignored in `.gitignore`. Both files should be created in the root directory. The files require the following keys (replace `{items}` in curly braces with your own values):
`env.properties`:
* `JDBC_PSQL_URI=jdbc:postgresql://{host}:{postgres_port}/{postgres_db_name}`
* `PSQL_USERNAME={postgres_username}`
* `PSQL_PASSWORD={postgres_password}`

`.env`:
* `DOCKER_SPRING_DATASOURCE_URL=jdbc:postgresql://{docker_service_name}:{docker_postgres_port}/{docker_postgres_db_name}`
* `DOCKER_POSTGRES_DB_NAME={docker_postgres_db_name}`
* `DOCKER_POSTGRES_USERNAME={docker_postgres_username}`
* `DOCKER_POSTGRES_PASSWORD={docker_postgres_password}`
* `GRAFANA_PASSWORD={grafana_password}`

Docker uses environment variables from a `.env` file by default. However, Spring Boot's `application.properties` throws an error if `.env` is used as the config file, so `env.properties` is used instead. The JAR file created does not include this `env.properties` file, so the Dockerfile includes a copy instruction to copy this file to the production folder.

On Github Actions, `env.properties` does not exist. Thus, Github Secrets are used instead. See [here](https://docs.github.com/en/actions/security-guides/encrypted-secrets#creating-encrypted-secrets-for-a-repository) for how to create secrets for a repository.

## Project structure
`/src/main/kotlin/com.example.blog/`:
`Advices.kt`: intercepts exceptions thrown by the application and sends HTTP responses back to the client
`BlogApplication.kt`: main entry point
`BlogConfiguration.kt`: configuration for the app
`BlogProperties.kt`: configuration properties for the app
`Entities.kt`: JPA representations of the tables in the database
`Exceptions.kt`: custom exceptions
`Extensions.kt`: utility functions
`HtmlController.kt`: controller for rendering the main blog page using Mustache templates
`HttpControllers.kt`: API controller
`Repositories.kt`: Spring CRUD repositories

## Database initialisation
The PostgreSQL docker image runs the files found in `docker/docker-entrypoint-initdb.d/`. In this application, it is used to simply create the tables and seed the database.

## Docker
During development, docker images were often not rebuilt when there was a change. `docker compose up --remove-orphans --force-recreate --build` was used to force rebuilding.

## Prometheus and Grafana
Prometheus and Grafana are included in the Docker Compose file. If `docker compose` is used to start the application, Prometheus will be exposed on port 1029, and Grafana on port 1040.

## Using H2 database instead
A H2 embedded database can also be used with the application. Simply remove `spring.datasource.url` from `application.properties` and replace `runtimeOnly("org.postgresql:postgresql")` with `runtimeOnly("com.h2database:h2")` in `build.gradle.kts`. By default, Spring uses a transient database in memory (RAM), that will be wiped upon the application restarting. `BlogConfiguration.kt` contains a `databaseInitializer` function that can be uncommented when using a H2 database to seed the database upon the application starting.

## `application.properties` explained
`spring.jpa.properties.hibernate.globally_quoted_identifiers=true` and `spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions = true` allow the entity called "User". In PostgreSQL, "User" is a reserved keyword, and normally cannot be used as a table name. However, these settings make Hibernate (Spring's ORM) escape any mentions of "User" by enclosing it in double quotes, allowing it to be a valid identifier.

`spring.jpa.hibernate.ddl-auto=update` creates the database tables if they do not exist. If they already exist, it checks the database tables and updates the schema (untested).

## Testing
`.env` is not committed, so a substitute `.env` file for testing is included in the `.github` folder. During the Windows test, the file is copied to the root directory before the tests are run.

## Appendix
`kapt`: annotation processor for Kotlin.
Logging: the `spring-boot-starter-logging` dependency depends on `spring-jcl`, which enables logging by default.
# Kotlin Blog
_A simple CRUD API_

[![Docker Compose](https://github.com/zxisatree/kotlin-blog/actions/workflows/docker.yml/badge.svg)](https://github.com/zxisatree/kotlin-blog/actions/workflows/docker.yml)
[![Gradle test on push](https://github.com/zxisatree/kotlin-blog/actions/workflows/test.yml/badge.svg)](https://github.com/zxisatree/kotlin-blog/actions/workflows/test.yml)

CRUD blog backend written with Spring Boot and Mustache for HTML templates. A temporary PostgreSQL database is included in the docker image.

## Why?
The aim of this project was to learn more about Spring Boot and CI/CD. Thus, the project is intentionally kept small and simple.

This repository can be used to resolve potential issues that might be found when using Spring Boot with Kotlin, which is not as common as Java and thus harder to find answers for.

## Building the application from scratch
The following prerequisites are required for this project:
* JDK 17
* Kotlin compiler
* Docker (or Docker Desktop for Windows)

Clone the repository, then run `docker compose up` in the root folder. The JAR file will be built and copied to the docker image. The server is exposed on port 1018 by default.

To build the application without Docker (and without a PostgreSQL database), run `./gradlew build -x test` in the root directory. The JAR file can be found in `/build/libs/` after building.

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

## Using H2 database instead
A H2 embedded database can also be used with the application. Simply remove `spring.datasource.url` from `application.properties` and replace `runtimeOnly("org.postgresql:postgresql")` with `runtimeOnly("com.h2database:h2")` in `build.gradle.kts`. By default, Spring uses a transient database in memory (RAM), that will be wiped upon the application restarting. `BlogConfiguration.kt` contains a `databaseInitializer` function that can be uncommented when using a H2 database to seed the database upon the application starting.

## `application.properties` explained
`spring.jpa.properties.hibernate.globally_quoted_identifiers=true` and `spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions = true` allow the entity called "User". In PostgreSQL, "User" is a reserved keyword, and normally cannot be used as a table name. However, these settings make Hibernate (Spring's ORM) escape any mentions of "User" by enclosing it in double quotes, allowing it to be a valid identifier.

`spring.jpa.hibernate.ddl-auto=update` creates the database tables if they do not exist. If they already exist, it checks the database tables and updates the schema (untested).

## Docker
During development, docker images were often not rebuilt when there was a change. `docker compose up --remove-orphans --force-recreate --build` was used to force rebuilding.

## Appendix
`kapt`: annotation processor for Kotlin
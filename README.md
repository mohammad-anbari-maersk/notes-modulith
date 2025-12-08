# Notes Modulith
Modular notes application showcasing Spring Modulith. It exposes REST APIs to manage users and their notes, and emits domain events when notes are created. Persistence is via PostgreSQL; asynchronous integration uses RabbitMQ.

You can visit [my Medium post](https://senoritadeveloper.medium.com/modular-monolith-with-spring-boot-spring-modulith-6687c234daab) to read the details about the implementation.

## Features
• Users: create and list users.

• Notes: create, update, list, fetch and delete notes.

• Domain events: publishing `NoteCreatedEvent` to Spring Modulith’s event bus with AMQP integration (RabbitMQ) and JDBC event publication log.

• Clear module boundaries: `users`, `notes`, `notifications`, `config`, `common`.

• Production‑ready extras: Actuator, structured logging, integration/unit tests, Testcontainers support.

# Installation
The easiest way to run dependencies is with Docker (PostgreSQL + RabbitMQ).

1) Start infrastructure

   From the `docker` directory:

   -  `docker compose -p notes up -d`

   Services started:
   - PostgreSQL: localhost:5432 (user: postgres / password: postgres, db: postgres)
   - RabbitMQ: AMQP localhost:5672, Management UI: http://localhost:15672 (guest/guest)

2) Run the application

   From the project root:

   - Using Maven: `./mvnw spring-boot:run`
   - Or build a jar: `./mvnw clean package` then `java -jar target/notes-modulith-0.0.1-SNAPSHOT.jar`

   Default app port: 8081, base context path: `/api` (see `src/main/resources/application.properties`).

# Technologies
Implemented with Spring Boot 4.0.0 and Java 25.

<img src="https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white" alt="Java">
<img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=fff" alt="Spring Boot">


Key libraries/modules:
- Spring Modulith 2.0.0 (core, JDBC, actuator)
- Spring Web MVC
- Spring Data JPA (Hibernate)
- PostgreSQL driver
- Spring AMQP (RabbitMQ)
- MapStruct for DTO mapping
- Lombok
- Testing: JUnit 5, Mockito, Spring Boot Test, Testcontainers

# Configuration
Relevant application settings are in `src/main/resources/application.properties`:
- Server: `server.port=8081`, `server.servlet.context-path=/api`
- Database: `spring.datasource.url=jdbc:postgresql://localhost:5432/postgres`
- RabbitMQ: `spring.rabbitmq.*` pointing at localhost defaults
- Modulith events: JDBC publication log under schema `events`

# Usage
Base URL: `http://localhost:8081/api`

Health check (Actuator):
```
curl http://localhost:8081/api/actuator/health
```

Users
```
# Create user
curl -X POST http://localhost:8081/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"Richard"}'

# List users
curl http://localhost:8081/api/users
```

Notes
```
# Create note (publishes NoteCreatedEvent)
curl -X POST http://localhost:8081/api/notes \
  -H 'Content-Type: application/json' \
  -d '{"title":"First","content":"Hello Modulith"}'

# List notes
curl http://localhost:8081/api/notes

# Get by id
curl http://localhost:8081/api/notes/1

# Update
curl -X PUT http://localhost:8081/api/notes/1 \
  -H 'Content-Type: application/json' \
  -d '{"title":"Updated","content":"Updated content"}'

# Delete
curl -X DELETE http://localhost:8081/api/notes/1
```

Also available in http_requests.http

- The same sample requests are available in the `http_requests.http` file at the project root.
- You can open this file in JetBrains IDEs (IntelliJ IDEA, WebStorm, etc.) and run individual requests via the built‑in HTTP Client.
- Defaults assume port `8081` and base context path `/api` (see `src/main/resources/application.properties`).

RabbitMQ Management UI: http://localhost:15672 (guest/guest). Note events are sent to AMQP via Spring Modulith AMQP integration.

# Development
- Run tests: `./mvnw test`
- Check Modulith docs generated under `target/spring-modulith-docs/` (e.g., `all-docs.adoc` and PlantUML diagrams).

## Authors / Contributors / Credits
**Nil Seri**

[Github 1](https://github.com/nilseri01)

[Github 2](https://github.com/senoritadeveloper01)

[Medium](https://senoritadeveloper.medium.com/)

## Copyright & Licensing Information
This project is licensed under the terms of the MIT license.


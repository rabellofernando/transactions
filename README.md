# Transaction Service

REST API for managing bank transactions.

## Technologies

- Java 21
- Kotlin 2.2.21
- Spring Boot 4.0.2
- PostgreSQL
- Docker
- Rest Assured + Test containers

## Architecture

The project follows **Hexagonal Architecture** and **Clean Architecture** concepts:

```
src/main/kotlin/com/transaction/
├── api/            # REST Controllers and configurations
├── domain/         # Business rules and entities
└── persistence/    # Data access (JPA)
```

**api/** - Entry layer. Controllers that handle HTTP requests.

**domain/** - Application core. Contains business logic, independent of frameworks.

**persistence/** - Infrastructure layer. Database communication.

This organization keeps business code isolated, making testing and maintenance easier.

## Features

- Create accounts
- Register transactions (purchase, installment, withdrawal, credit)
- CPF/CNPJ validation

## Endpoints

```
POST   /accounts                    # Create account
GET    /accounts/{id}               # Get account
POST   /transactions                # Create transaction
GET    /actuator                    # Actuator endpoints such as /health for readiness and liveness probe
```

Full documentation: `http://localhost:8080/swagger-ui.html`

## Run

### With Docker

```bash
./start.sh
```

### Local

```bash
./gradlew bootRun
```

Application available at `http://localhost:8080`

## Tests

```bash
./gradlew test
```

## Notes

- Transaction amounts are automatically converted (purchases become negative, credits positive)
- CPF, CNPJ or "documents" in general are validated and accepted with or without formatting
- Documents are stored without formatting in the database
- Next steps would be migrating all the "migration" part onto a different project / pipeline, using tools such as db-migration, versioning the scripts. with the application version is not a good practice, but since we're talking about tests, it's allowed. :P
- Add a cache layer on account entity, starting by @Cacheable, and maybe use a cache provider such as memcached or redis instead of caching it locally on its instance
- Maybe create a new project using either cucumber or karate for bdd tests instead of rest assured + test containers on this one

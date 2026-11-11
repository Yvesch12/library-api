# Lab 4 - Design and deployment

## Design review

The full pre-change findings are in `docs/lab4-design-review.md`. CRITICAL:
Book/Member controllers bypassed services, and all controllers exposed JPA entities.
IMPORTANT: duplicated local exception handlers, no consistent timestamp/field errors,
and undocumented HTTP failures. MINOR: dense formatting/wildcard imports.
Direct repository dependencies couple transport to persistence; introducing services
gives transaction/application behavior one cohesive home. Entity exposure couples
the external API to table mappings and risks unintentionally exposing new fields.

## DTO decision

BookDTO and MemberDTO preserve their existing JSON fields. BorrowingDTO exposes
bookId/memberId instead of nested entities; adding a private Member property later
will no longer silently add it to borrowing responses. ReservationDTO follows the
same principle. Controllers have no entity types in their method signatures.
The mapper boundary makes contract changes explicit while allowing internal
JPA relationships and persistence annotations to evolve independently.

## Patterns

Repository: BookRepository extends JpaRepository and abstracts database CRUD.
Service/Facade: BorrowingService coordinates book availability, member validation,
loan creation, return dates, and fine calculation behind a small application API.
DTO/Mapper: BookDTO and BookMapper separate transport representation from Book.
Constructor injection makes dependencies explicit and enables isolated Mockito tests.

## Deployment

Dockerfile builds with maven:3.9-eclipse-temurin-17 and runs a non-root user on
eclipse-temurin:17-jre-alpine with -Xmx256m/-Xms128m. Multi-stage builds keep Maven,
source, and build caches out of the runtime image. Docker is not installed on this
Windows host, so a local container build/run is NOT claimed. The GitHub workflow
builds the image and checks container HTTP responses when Actions is available;
its actual outcome must be checked rather than assumed.
PostgreSQL would require its JDBC runtime dependency, external connection settings
and credentials, a managed schema/migration policy, database-specific integration
tests, and removal of H2-specific console/development seed configuration.

## Verification

Shared handler integration tests cover missing book -> 404, borrowed book -> 409,
invalid fields -> 400, and malformed JSON -> 400. OpenAPI routes are now at
`/api-docs`, with Swagger UI at `/swagger-ui.html`; historical Lab 1 uses `/v3/api-docs`.
All endpoint methods have Operation/ApiResponses and all DTO fields have Schema examples.
The suite passed 24 tests with zero failures/errors. An initial check exposed
Spring Boot's default problem-details advice taking precedence for validation;
ordering the custom advice first fixed the response shape and the regression test.

## Subsequent container verification

GitHub run [43100862867](https://github.com/Yvesch12/library-api/actions/runs/43100862867)
passed Maven verification, Docker image build, and container HTTP checks. The initial
workflow requested HTTP before application startup completed and received curl
error 56 (connection reset). Commit ba4ca9b added a bounded health-readiness loop
before checking five books, three members, and OpenAPI JSON. This is real container
execution on GitHub's Linux runner; local Docker installation remains unnecessary
for this verification path.

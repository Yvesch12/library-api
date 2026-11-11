# Design review before Lab 4

| Finding | Severity | Evidence and change |
|---|---|---|
| Controller/repository coupling | CRITICAL | BookController and MemberController call repositories directly; introduce services |
| Entities in API contracts | CRITICAL | All four controllers return managed JPA entities; introduce explicit DTOs/mappers |
| Duplicated local error handlers | IMPORTANT | BorrowingController and ReservationController each catch 404/409; replace with shared advice |
| Missing error metadata | IMPORTANT | Local maps contain error only; introduce timestamp and field-error map |
| Incomplete API documentation | IMPORTANT | 404 displayed as Undocumented in the student's Swagger screenshot; annotate operations/statuses |
| Dense formatting and wildcard imports | MINOR | New service/controller imports and short methods; prefer readability in later cleanups |

CRUD services preserve existing semantics. Borrowing and reservation rules stay
in their services; controllers only handle transport, mapping, and HTTP statuses.
DTOs contain relation IDs instead of nested persistence objects. Error messages
for unexpected exceptions deliberately omit SQL and implementation details.

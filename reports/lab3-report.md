# Lab 3 - Requirements and specification

The spec and implementation were produced by Codex under the student's instruction
to finish the labs. No student-written spec or separate plan approval is claimed.

## Specification audit

| Rule | Classification | Implementation evidence |
|---|---|---|
| R1 existing book/member, 404 otherwise | Precise enough | reserve uses findById/orElseThrow |
| R2 unavailable book, exact conflict message | Precise enough | reserve checks isAvailable; AC-1 exact message |
| R3 active membership | Precise enough | reserve checks isActive |
| R4 one ACTIVE pair | Precise enough for sequential requests | existence query and duplicate test |
| R5 ACTIVE and today's date | Precise enough | explicit assignments in reserve |
| R6 cancel once | Precise enough | cancel rejects non-ACTIVE status |
| R7 all statuses, ID order, missing 404 | Precise enough | ordered repository query and member existence guard |
| R8 no availability changes, re-reservation allowed | Precise enough | service changes reservation only; re-reservation test |

Observed gaps, not silently fixed in this lab: R4 specifies logical uniqueness but
not concurrent isolation, so two simultaneous creates can race; no authorization
policy defines who may cancel a reservation. Queue position, expiry, and automatic
allocation are explicitly outside the specification. A production requirements
review should resolve concurrency and access control before deployment.

## Plan confirmation

The preimplementation plan in `docs/lab3-plan.md` states: generated Long ID,
Book/Member ManyToOne references, LocalDate reservedDate, enum ACTIVE/CANCELLED;
POST /reservations, GET /members/{id}/reservations, PUT /reservations/{id}/cancel;
new repository, transactional service, controller, acceptance tests; enforce R1-R8.
No clarification was requested because sequential cancellation and listing semantics
were explicit. This does not imply completeness for real users. The student's
blanket authorization replaced interactive approval, and this distinction is recorded.

## Functional and non-functional requirements

Purpose, R1-R8, entity fields, endpoint contracts, and AC-1 through AC-5 are functional:
they define stored information and observable behavior. Package conventions,
constructor injection, dependency restrictions, no migrations, and transaction
placement are implementation/maintainability constraints, not user features.
Exact statuses and example outcomes were easier to specify than broad qualities
such as maintainability, which need measurable criteria to avoid ambiguity.

## Specification-driven versus open prompt

Lab 1 required a later comparison against the handout to complete the initial
skeleton. Here the written rules fixed error messages, duplicate behavior,
cancellation, and sorting before implementation. The 21-test green suite includes
ReservationServiceTest AC tests and an HTTP acceptance flow in
ReservationControllerTest. Structured specifications make omissions easier to see
and test, but do not guarantee that missing real-world requirements are discovered.

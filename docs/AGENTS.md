# Library API: Book Reservation specification

## Scope

These requirements govern the Lab 3 reservation feature, not unrelated later labs.
Spring Boot 3; existing Book, Member, Borrowing; packages under com.example.library.

## Purpose

A member can register interest in a book held by another member. A reservation
records that interest until cancelled; this lab does not implement allocation,
notifications, expiry, or a reservation queue.

## Business rules

- R1: A reservation requires existing Book and Member IDs; unknown IDs return 404.
- R2: The book must be unavailable; otherwise return 409 with exactly
  "Book is available for direct borrowing." as the error message.
- R3: The member must be active; otherwise return 409 "Member is inactive".
- R4: At most one ACTIVE reservation per book/member pair; duplicates return 409.
- R5: New reservations have status ACTIVE and reservedDate equal to today's date.
- R6: Cancellation changes ACTIVE to CANCELLED; repeated cancellation returns 409.
- R7: Listing a member's reservations returns all statuses in ascending ID order;
  unknown members return 404, members without reservations get an empty list.
- R8: Reservations do not change availability or existing loans. No fulfilment is
  automatic; a cancelled pair may be reserved again if R1-R3 still hold.

## Entity

Reservation: Long id (generated), Book book (@ManyToOne), Member member
(@ManyToOne), LocalDate reservedDate, ReservationStatus status (string enum:
ACTIVE, CANCELLED). No reverse collections.

## Endpoints

| Method | Path | Body | Response | Status codes |
|---|---|---|---|---|
| POST | /reservations | {bookId: Long, memberId: Long}, positive/non-null | reservation | 201,400,404,409 |
| GET | /members/{id}/reservations | none | reservation array | 200,404 |
| PUT | /reservations/{id}/cancel | none | cancelled reservation | 200,404,409 |

## Acceptance criteria

- AC-1: An available book produces 409 and the exact R2 message.
- AC-2: An unavailable book and active member produce 201, ACTIVE status, today's
  date, and correct book/member references without changing availability.
- AC-3: A second ACTIVE reservation for the same pair produces 409 and no extra row.
- AC-4: Cancelling produces CANCELLED; listing includes it; repeating cancel produces 409.
- AC-5: Missing book/member/reservation produces 404; inactive member produces 409.

## Constraints

For Lab 3 do not modify existing entities or controllers, add dependencies, or
introduce Flyway/Liquibase. Add new files for reservation code and tests only.
Follow entity/repository/service/controller packages, constructor injection,
Long IDs, and existing local 404/409 exception handling style. Transactions belong
in services. No claims of concurrency safety without concurrency tests.

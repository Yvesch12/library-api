# Lab 5 - Refactoring

## Baseline and smell catalog

Baseline: 24 tests, all passing at lab-4. Inspection found the following concrete
maintenance concerns (severity is contextual, not a claim that each is a defect):

| Smell | Evidence before refactoring | Severity | Response |
|---|---|---|---|
| Magic Numbers | `today.plusDays(14)` and `new BigDecimal("0.50")` | Medium | Replace Magic Number with named constants |
| Dense expression | `Math.max(0, ChronoUnit.DAYS.between(dueDate, returnDate))` inside multiply | Low | Extract Method daysOverdue |
| Feature Envy | returnBook calls `loan.getBook()` repeatedly to update/save another object | Medium | Move availability manipulation into a helper accepting Book |
| Duplicate Code | missing entity `findById(...).orElseThrow(...)` repeated across services | Low | Candidate shared lookup policy; left explicit to avoid generic abstraction |
| Primitive Obsession | Long IDs in LoanRequest are distinguished only by names | Low | Candidate typed IDs; deferred to preserve a simple transport contract |
| Long line/readability | BorrowingService missing-resource expressions combine lookup, lambda, exception | Low | Candidate multiline formatting; not a behavioral defect |

No god class or private-field intimacy was demonstrated; inventing these findings
would not be a useful review. BorrowingService is not a Long Method under the
handout's >15-lines threshold merely because one line is long.

## Three refactorings

1. Extract Method: `calculateFine` now calls `daysOverdue`. Lab 2 already required
   a public pure calculateFine method, so making it private would break its testable
   API; the nested day computation was extracted instead. The nonnegative day rule
   has a name and can change independently of decimal multiplication.
2. Replace Magic Number: DAILY_FINE_RATE and DEFAULT_LOAN_DAYS replace 0.50 and 14.
   The names document business meaning and provide one place to update policy.
3. Extract Method: availability changes use `markAvailable(Book, boolean)` so
   returnBook no longer repeatedly reaches through Borrowing to update/save Book.
   This reduces duplicated persistence/availability mechanics without moving
   transaction ownership out of BorrowingService.

The exact before/after diff is in `docs/lab5-refactor.diff`. Changes preserve the
same fine arithmetic, due-date duration, response fields, and exceptions.

## AI-generated debt

The generated code had policy literals and dense expressions despite working tests.
These are concrete reasons for preventive maintenance, not evidence that all
AI-written code is worse than careful human code: no matched human implementation
was measured. The four service classes have distinct responsibilities; a global
rewrite would increase risk without addressing the observed small smells.

## Regression safety

The full `mvn test` suite was run after each transformation, not only at the end.
Each run passed 24 tests. The fine boundary test would catch changes to the daily
rate and handling of early/on-time returns; borrowBook tests protect the 14-day
deadline and availability, while the return test checks a 3-day fine of 1.50 and
book availability. Tests protect these specific behaviors, not concurrency or
every possible client interaction.

## CI status

The CI workflow is committed but no successful container run is asserted. Automatic
approval review rejected reading a stored Git credential for a CI API query; the
credential-reading helper was removed and that operation was not retried at the
Lab 5 checkpoint. The student later explicitly authorized a read-only CI status
check; it confirmed the workflow was queued, not that the container had passed.

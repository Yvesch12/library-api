# Generated test audit

This audit is AI-generated; it is not represented as the student's manual audit.
Each test checks a required behavior rather than comparing a value to itself.

| Test | Classification | Reason / improvement |
|---|---|---|
| borrowHappyPathPersistsAvailabilityAndDeadline | Meaningful | Checks generated ID, dates, references, zero fine, writes and availability |
| missingBookDoesNotWrite | Meaningful | Exception plus no member/loan access or book write |
| missingMemberDoesNotChangeAvailability | Meaningful | Protects availability and forbids writes |
| alreadyBorrowedDoesNotWrite | Meaningful, strengthen | Add book-save prohibition and unchanged unavailable state |
| inactiveMemberDoesNotWrite | Meaningful, strengthen | Add book-save prohibition; loan write already prohibited |
| returnOnTimeHasZeroFineAndRestoresBook | Meaningful | Exact 0.00, return date, availability, persistence |
| returnThreeDaysLateChargesOneFifty | Meaningful | Independently calculated 3 x 0.50 = 1.50 |
| returnExactlyOnDueDateHasZeroFine | Meaningful | Boundary behavior at zero days |
| missingBorrowingDoesNotWrite | Meaningful | Missing ID and no book/write side effects |
| repeatedReturnPreservesOriginalDateAndFine | Meaningful, strengthen | Add loan-save prohibition |
| activeLoansUseMemberFilter | Meaningful | Verifies exact repository filtering and result |
| missingMemberCannotListLoans | Meaningful | Missing branch, repository not queried |
| fineBoundariesUseExactDecimalAmounts (4 invocations) | Meaningful | Early, exact, one-day, seven-day arithmetic |

Summary: 13 test methods / 16 invocations; all meaningful, none with a wrong oracle,
none assertion-free. Meaningful does not imply exhaustive. Three existing assertions
were strengthened as above; no deliberately weak generated test is invented for
the report. Missing scenarios: actual HTTP serialization/persistence across
requests; complete return cycle; duplicate borrowing HTTP conflict. Added three
RANDOM_PORT/TestRestTemplate tests for these flows. Concurrency and clock/midnight
behavior remain untested rather than being claimed as covered.

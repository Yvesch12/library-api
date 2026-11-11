# Lab 7 - Testing and evals

## Coverage and verification

| Measure | Lab 6 final baseline | Lab 7 final |
|---|---:|---:|
| Overall line | 75.79% (241/318) | 89.94% (286/318) |
| Overall branch | 94.44% (17/18) | 100% (18/18) |
| Service line | 100% (61/61) | 100% (61/61) |
| Service branch | 94.44% (17/18) | 100% (18/18) |

The service layer already exceeded the 80% line target; adding tests was justified
by missing assertions/branches and actual HTTP contracts rather than chasing a
larger line percentage. Final `mvn clean verify`: 44 tests, zero failures/errors.
Raw class counters: docs/lab6-after-metrics.json and docs/lab7-metrics.json.

## Audit

The full 13-method classification table is in docs/lab7-test-audit.md: 13 meaningful,
0 weak, 0 wrong-oracle methods (16 parameterized invocations in total). Three
improvements forbid book writes on already-borrowed/inactive requests and forbid
loan writes on repeated returns. New tests cover previously missing full HTTP
serialization, the return cycle, and duplicate borrowing conflicts.
This was an AI audit and implementation, not the manual student audit requested
in the handout; no claim of student authorship is made.

An actual integration defect was discovered: Jackson READ_ONLY on DTO IDs caused
TestRestTemplate's DTO response deserializer to ignore generated IDs. IDs now use
OpenAPI Schema READ_ONLY metadata; create services still clear supplied IDs and
PUT uses the path ID. Existing ID-injection regression tests still pass.

## Deliberate bug experiment

Both mutations were run only after a confirmed 44-test passing baseline and the
source was restored in a finally block. The final clean build passed again.
The earlier exploratory run had unrelated HTTP failures, so it is NOT used as
evidence of mutation detection. docs/lab7-mutations.json contains the valid run's
complete passed and failed test lists; scripts/mutation-check.py reproduces it.

| Bug | Detected by | Examples that remained green |
|---|---|---|
| Rate 1.00 instead of 0.50 | 5: fineBoundaryCases; returnBookMakesAvailableAndChargesFine; returnThreeDaysLateChargesOneFifty; fine parameter cases 1/7 days | missing-book/member tests, borrow deadline, on-time zero fine, reservation tests |
| Null instead of zero for early/on-time return | 7: fullReturnCycleRestoresAvailability; fineBoundaryCases; activeBorrowingsExcludeReturnedAndOtherMembers; returnExactlyOnDueDateHasZeroFine; fine parameter cases -1/0 days; returnOnTimeHasZeroFineAndRestoresBook | overdue arithmetic, missing-ID errors, ordinary borrow flow, reservation tests |

The active-borrowing integration test also fails under the null mutation because
returning a loan persists a non-null fine column; this is detection by an invariant,
not a direct fine assertion. Unit tests localize the arithmetic defect more precisely.

## Evals connection

The reservation acceptance criteria define inputs and observable outcomes, so
they serve as test oracles and as evaluations of whether AI-generated code meets
its specification. AC-1 requires exactly a 409 and its stated message for an
available book; a 201, different message, or a stored reservation would be a
failing evaluation even if compilation and unrelated tests pass. This links
requirements to executable checks, while leaving unspecified requirements such
as authorization outside the measured guarantee.

## Trust and limitations

AI tests need review: the strengthened already-borrowed test previously checked
the exception and loan writes but did not prohibit book writes, and the repeated
return test initially omitted a loan-save prohibition. The HTTP client-ID failure
also showed that mock-only tests missed a serialization boundary. Exact expected
fine amounts and the two mutation detections provide stronger evidence than
coverage alone, but concurrency, midnight clock changes, and real PostgreSQL are
still not validated. The student should not present these generated observations
as a personal manual review that did not take place.

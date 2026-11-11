# GQM measurement plan

## Goal

Analyse the service layer in order to improve maintainability with respect to
decision complexity and regression protection from the developer's point of view.

## Questions

Q1: Are BorrowingService methods simple enough to inspect and test independently?
Q2: Do tests execute the business decisions, including conflict and missing-ID paths?

## Metrics

M1 (Q1): manually count decision points plus one for each BorrowingService method;
count if, loops, catch, conditional operators, && and ||; exclude library internals
and lambda expressions without decisions. See the Lab 6 complexity table.
M2 (Q2): use JaCoCo line and branch counters for the service package after mvn test;
use XML numbers, not the number of test methods as a proxy for coverage.

## Results and interpretation

Measured values are recorded in `docs/lab6-metrics.json` and `reports/lab6-report.md`.
Lab 7 adds error-path tests and repeats the measurements in `docs/lab7-metrics.json`.
Low method complexity makes inspection easier but cannot demonstrate correct
business rules; coverage needs assertions and deliberate mutation experiments.

M1 actual: BorrowingService constructor 1, borrowBook 3, returnBook 2,
activeBorrowings 2, calculateFine 1, daysOverdue 1, markAvailable 1 (all Simple).
M2 actual: Lab 6 service lines 61/61 (100%), branches 17/18 (94.44%);
Lab 7 service lines 61/61 (100%), branches 18/18 (100%). Overall line coverage
rose from 75.79% to 89.94%. Two controlled fine mutations were both detected.

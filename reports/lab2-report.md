# Lab 2 - Agile sprint

This is an AI-executed sprint authorized by the student; it is not evidence of
manual student test authorship or personal time tracking.

## Velocity

| Story | Codex estimate (h) | Actual |
|---|---:|---|
| US-01 borrowing | 0.75 | Shared session |
| US-02 returns | 0.50 | Shared session |
| US-03 active loans | 0.25 | Shared session |
| US-04 fines | 0.25 | Shared session |

Total estimate 1.75 h; measured shared agent session 0.039 h; ratio 0.022.
This is below 1, but compares automated elapsed time with an agent planning
estimate, not observed human productivity. Per-story student time was not supplied.
The canonical backlog is `docs/sprint-backlog.md`.

## TDD observation

Commit `de6e233` contains the tests before BorrowingService existed. The actual red
run failed `testCompile` with `cannot find symbol: class BorrowingService`.
The green run completed 15 tests with no failures. Defining expected dates,
availability changes, and decimal fine boundaries first constrained the design
before any implementation existed. These tests were written by Codex.

## XP pair review

Review output: (1) reject inactive members; (2) reject repeated returns;
(3) map missing resources and conflicts; (4) concurrent check/update is a race;
(5) inject a clock for deterministic midnight behavior; (6) do not describe
AI-authored tests as manual student work. Full review: `docs/lab2-review.md`.
Suggestions 1 and 2 were applied with tests because they prevent invalid loans
and changing a completed return. Local HTTP handlers implement suggestion 3.
Microservices/event sourcing were rejected as unnecessary for the four stories;
concurrency control and clock injection remain explicitly documented concerns.

## AI and estimation

The observed session was shorter than the planning estimate, but no valid
human-versus-AI speedup can be inferred without a human baseline. Reuse of the
build and testing conventions may reduce setup time in the next sprint; new
requirements and review can offset that gain. Personal estimates, actual effort,
and the student's own prediction must be distinguished from this measured run.

# Lab 6 - Measures and metrics

## Reproducible analysis

Configured the assignment's versions: PMD plugin 3.21.0 (PMD 6.55.0, quickstart
rules, Java 17, non-failing violations), SpotBugs 4.8.3.1 (Max/Low),
JaCoCo 0.8.11 (agent and report bound to the test lifecycle). Actuator adds the
stack's health endpoint. Commands: `mvn test pmd:pmd spotbugs:spotbugs pmd:check`.
Reports: target/pmd.xml, target/spotbugsXml.xml, target/site/jacoco/index.html.
Raw pre-fix analyzer snapshots are committed under docs/lab6-*-before.xml.

## Baseline coverage by class

| Class | Covered/total lines | Coverage |
|---|---:|---:|
| LibraryApiApplication | 1/3 | 33.33% |
| BookDTO | 16/16 | 100% |
| LoanRequest | 1/1 | 100% |
| ReservationDTO | 11/11 | 100% |
| BorrowingDTO | 0/15 | 0% |
| MemberDTO | 14/14 | 100% |
| MemberMapper | 15/15 | 100% |
| BorrowingMapper | 1/23 | 4.35% |
| BookMapper | 19/19 | 100% |
| ReservationMapper | 8/19 | 42.11% |
| BorrowingController | 1/5 | 20% |
| BookController | 7/7 | 100% |
| MemberController | 5/5 | 100% |
| ReservationController | 5/5 | 100% |
| BookService | 10/10 | 100% |
| BorrowingService | 28/28 | 100% |
| ReservationService | 19/19 | 100% |
| MemberService | 4/4 | 100% |
| Member | 15/22 | 68.18% |
| Reservation | 10/11 | 90.91% |
| ReservationStatus | 1/1 | 100% |
| Borrowing | 22/26 | 84.62% |
| Book | 17/26 | 65.38% |
| GlobalExceptionHandler$ErrorResponse | 1/1 | 100% |
| GlobalExceptionHandler | 8/10 | 80% |
| ReservationRepository | 0/0 | N/A |
| MemberRepository | 0/0 | N/A |
| BookRepository | 0/0 | N/A |
| BorrowingRepository | 0/0 | N/A |

Overall 75.63% line and 94.44% branch;
service 100% line and 94.44% branch.
BorrowingDTO has the lowest coverage (0%) because the existing HTTP tests only
exercise failing borrowing requests, not successful DTO responses. The tests of
BorrowingService run below the DTO boundary. Lab 7 must add full HTTP flows.

## Manual cyclomatic complexity

CC = 1 plus decisions in the method's own body; orElseThrow lambdas do not add
branches, and Math.max/ChronoUnit internals are not counted as local decisions.

| BorrowingService method | Decisions | CC | Risk |
|---|---:|---:|---|
| constructor | 0 | 1 | Simple |
| borrowBook | 2 if statements | 3 | Simple |
| returnBook | 1 if statement | 2 | Simple |
| activeBorrowings | 1 if statement | 2 | Simple |
| calculateFine | 0 | 1 | Simple |
| daysOverdue | 0 | 1 | Simple |
| markAvailable | 0 | 1 | Simple |

No method exceeds 5. A short method can still have a concurrency defect or wrong
business rule; low CC is not a correctness proof.

## Coupling

| Class | Ce | Ca | I = Ce/(Ce+Ca) |
|---|---:|---:|---:|
| BookService | 6 | 1 | 0.857 |
| BorrowingService | 13 | 1 | 0.929 |
| MemberService | 6 | 1 | 0.857 |
| ReservationService | 12 | 1 | 0.923 |

scripts/metrics.py counts unique imported classes (including JDK/framework),
expands used project wildcard imports, and counts main-source callers, excluding
tests. This is the assignment's import-based heuristic, not a complete bytecode
dependency graph. BorrowingService is most unstable (0.929), reflecting an
application coordinator with three repository dependencies and domain/date types.
Its ratio is expected for this role; it does not justify combining all services.

## Actual finding and fix

Before: PMD reported 15 violations (unused imports, empty constructor documentation,
and the Spring entry point utility-class heuristic); SpotBugs reported 13
priority-2 representation-exposure warnings. An IMPORTANT, actionable pair was
ErrorResponse storing a mutable caller-provided Map and returning the same map.
Its compact record constructor now uses Map.copyOf, preventing mutation through
the original input or the accessor. ErrorResponseTest checks both paths.
Unused DTO imports were also removed. JPA relationship exposure warnings are
documented persistence-model tradeoffs; copying managed entities in their accessors
would break identity semantics and is not used as a superficial analyzer fix.

After-fix counts and coverage are recorded in docs/lab6-analysis-results.json and
docs/lab6-after-metrics.json. The targeted ErrorResponse warnings must be absent.

## GQM and AI interpretation

See docs/gqm.md for Goal, Questions, Metrics, and interpretation. Baseline AI-written
BookService and BorrowingService have 100% line coverage but different coupling
(0.857 vs 0.929); service branch coverage is 94.44%, showing line coverage alone
can hide an untested decision. There is no manually written comparison cohort,
so these values cannot support a claim that AI code is better/worse than human code.
Unused imports are evidence of generator cleanup debt, not an inflated productivity
measurement. Raw physical LOC also depends on formatting and accessor boilerplate.

Sources: [PMD rulesets](https://maven.apache.org/plugins/maven-pmd-plugin/examples/usingRuleSets.html),
[JaCoCo Maven](https://www.jacoco.org/jacoco/trunk/doc/maven.html),
[SpotBugs Maven](https://spotbugs.github.io/spotbugs-maven-plugin/usage.html).

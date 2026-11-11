# Lab 8 - Estimation and cost

## Scope and provenance

This is a transparent AI-assisted analysis, not invented student time tracking.
The table uses the handout's estimated lab hours and measured agent-session wall
time for Labs 2-8. Lab 1 personal/agent effort was not tracked; commit timestamps
do not measure time spent working. The student did not provide personal
predictions, actual hours, or final function-point classifications.
Lab 8 time below is measured through this analysis checkpoint, not claimed to
include subsequent Git pushes or waiting for external CI.

## Function point inventory

Boundary: the library API application. Technical health, Swagger, and H2 console
are not user business functions. CRUD outputs are counted with the input/query
that produces them, avoiding duplicate counting.

| Type | Function | Complexity | Weight |
|---|---|---|---:|
| EI | POST /books | Simple | 3 |
| EI | PUT /books/{id} | Average | 4 |
| EI | DELETE /books/{id} | Simple | 3 |
| EI | POST /members | Simple | 3 |
| EI | POST /borrowings | Average | 4 |
| EI | PUT /borrowings/{id}/return | Average | 4 |
| EI | POST /reservations | Average | 4 |
| EI | PUT /reservations/{id}/cancel | Simple | 3 |
| EQ | GET /books | Simple | 3 |
| EQ | GET /books/{id} | Simple | 3 |
| EQ | GET /members | Simple | 3 |
| EQ | GET /members/{id} | Simple | 3 |
| EQ | GET /members/{id}/borrowings | Average | 4 |
| EQ | GET /members/{id}/reservations | Average | 4 |
| ILF | Book | Simple | 7 |
| ILF | Member | Simple | 7 |
| ILF | Borrowing | Average | 10 |
| ILF | Reservation | Simple | 7 |
| EO | No separate derived/reporting output | None | 0 |
| EIF | No externally maintained logical business file | None | 0 |

EI 28 + EQ 20 + ILF 31 = **79 UFP**. Basic single-entity inputs/reads are simple;
multi-entity checks, status transitions, and filtered relationship lists are
average. Borrowing has more field groups and lifecycle data than Book/Member.
These are educational judgments, not a certified IFPUG count with complete DET/FTR
and RET worksheets. Ordinary GET retrieval is classified as EQ, not counted again
as EO. H2 is the internal storage technology, and OpenAPI is generated metadata;
neither is an externally maintained business file, despite the handout's suggested
EIF examples. No search endpoint or monthly fines report exists yet.

At 9 FP/person-day: 79 / 9 = **8.778 days**;
8.778 x 7.5 = **65.833 hours**. At the 8-10 FP/day range:
59.25-74.06 hours.
This reference estimate is for development of the business application, whereas
lab hours also cover learning, reflection, and tooling: the scopes are different.

## Estimated versus actual

| Lab | Handout estimate (h) | Measured agent wall time (h) | Agent/handout ratio |
|---|---:|---:|---:|
| 1 | 2.00 | Not tracked | N/A |
| 2 | 2.50 | 0.039 | 0.016 |
| 3 | 2.50 | 0.029 | 0.012 |
| 4 | 3.00 | 0.066 | 0.022 |
| 5 | 2.50 | 0.042 | 0.017 |
| 6 | 2.50 | 0.072 | 0.029 |
| 7 | 3.00 | 0.095 | 0.032 |
| 8 | 2.50 | 0.023 | 0.009 |
| Known subset 2-8 | 18.50 | 0.366 | 0.020 |
| All 8 labs | 20.50 | Not available | Not available |

The measured subset's inverse ratio is 50.55, but it is **not a
personal velocity multiplier**: handout learning estimates and automated wall
time measure different things. Recording zeros for unknown personal time would
produce a false result.

DeMarco EQF is area under actual divided by area of absolute forecast error.
For a constant forecast E and actual A over the same interval it reduces to
A / |E-A|. An explicitly illustrative constant-forecast proxy using the known
agent subset gives 0.366 / |18.5 - 0.366| =
**0.0202**. This is not the student's total
EQF: personal actuals and the lecture's forecast history were not supplied.
See [the research definition](https://citeseerx.ist.psu.edu/document?doi=02070322e5451d842c624cc978b3d2dfff83f5e1&repid=rep1&type=pdf).

## Hypothetical Fine Payment module

Requirements: GET /members/{id}/fines lists outstanding assessed fines with
borrowing IDs and amounts; POST /fines/{id}/pay marks an unpaid fine as paid and
records payment time; GET /reports/fines?month=YYYY-MM totals payments by month.
Unknown resources return 404; malformed months return 400; repeated payment returns
409; use BigDecimal, a transaction, validation, and a separate payment record.
No external card processor or money transfer is implied; authentication,
authorization, and audit policy require separate specification.

Production Java LOC estimate (excluding tests/config): min 120, best 200, max 340.
This range assumes reuse of existing DTO, repository, service, and handler patterns;
payment provider integration would exceed it. Three-point size =
(120 + 4 x 200 + 340) / 6 = **210 LOC = 0.210 KLOC**.

Organic COCOMO 81 using the handout:
- HM = 2.4 x 0.210^1.05 = **0.4662 person-months**.
- TDEV = 2.5 x HM^0.38 = **1.8706 months**.
- Team size = HM/TDEV = **0.2492 people**.

Fractional staffing is a model average, not a literal team recommendation, and
this tiny module is outside a persuasive empirical calibration range.

## LOC critique and modified alpha

Measured main Java: **865 physical lines, 797 nonblank lines, 28 Java files**.
Using only the observed agent subset denominator yields 865/0.366
= **2363.39 LOC/agent-hour**, and 79/0.366
= **215.85 FP/agent-hour**. These are illustrative mixed-scope
rates, not valid whole-project personal rates, because Lab 1 effort is missing.
Personal LOC/hour and FP/hour therefore remain unavailable.

LOC rewards adding DTO accessors, imports, and formatting even if no user function
is added. FP better describes delivered behavior, but also depends on boundary and
complexity judgments. A defensible productivity comparison needs the same scope,
developer effort measurements, review time, and quality outcomes.

If one mechanically rescales alpha by the known agent/handout ratio, alpha' =
2.4 x 0.01978 = **0.04748**. This is an illustrative
calculation only: heterogeneous learning estimates and agent wall time do not
calibrate a COCOMO constant. There is no evidence here supporting a new predictive
alpha value for future projects.

## Lifecycle reflection

The sequence specified borrowing behavior, wrote tests, implemented services,
formalized reservations, separated transport from persistence, refactored with
regression checks, measured coverage/static findings, and estimated a possible
extension. AI accelerated producing artifacts but also produced a real DTO client
deserialization issue caught only by full HTTP tests. Requirements, test oracles,
review, deployment evidence, and honest measurement still determine whether the
work meets its intended purpose. This paragraph describes observed events; it is
not a substitute for the student's own account of what they learned.

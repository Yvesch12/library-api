# Lab 1 - Introduction to AI-Assisted Engineering

Date: 2026-11-11

Prepared with Codex using the implementation and recorded session as evidence.
The student should read these reflections before submitting; automated checks do
not establish personal understanding or review.

## Observation

Codex separated the code into `entity`, `repository`, and `controller` packages
under `com.example.library`; I agree with this structure because responsibilities
are easy to locate and Spring discovers the classes below the application package.
It chose `LocalDate` for dates and `BigDecimal` for `fineAmount`; I agree because
the lab uses calendar dates and monetary values should avoid floating-point rounding.
It added two `@ManyToOne` relationships in `Borrowing`; I agree because each borrowing
references one book and one member, while either can appear in many historical borrowings.
I would keep these choices for this lab because they match its fields and relationships
without introducing a more complicated architecture than the initial CRUD API needs.

## Engineering judgment

Accepting the first skeleton without comparing it to the assignment would have left
out all three entities, the repositories, sample data, the required endpoints, and Swagger UI.
One configuration that needed verification was `spring.jpa.defer-datasource-initialization=true`,
which makes Hibernate create tables before `data.sql` inserts the sample records.
Another observed issue was that the terminal initially could not find Maven, although
it was installed, because the terminal retained its earlier environment settings.
The verification therefore covered sample JSON responses, invalid input, missing IDs,
and attempts to supply an existing ID during creation, since a successful build alone
does not demonstrate that the assignment is satisfied.

## Chapter connection

Codex helps with programming-in-the-small by producing classes, annotations, and
individual endpoint methods from a specification.
It also supports programming-in-the-large by organizing layers, configuring a build,
writing documentation, and checking interactions across HTTP, persistence, and the database.
The engineer still chooses scope, reviews assumptions, validates evidence, and owns
the repository and submission.
AI changes how quickly some activities can be performed, but it does not remove
the distinction between writing a method and managing a software lifecycle.

## Role reflection

The closest conceptual match is the role that defines the task and evaluates AI output,
while the assistant produces implementation details; the exact lecture table was not
provided, so this is a description of the role rather than a quotation of a row.
I specified Java 17 and Spring Boot 3, required approval before environment changes,
reported that Maven was not recognized, and supplied the assignment to establish the scope.
Codex generated the application and ran checks, but my report of the failed command
showed why its earlier success message still needed to be checked in my own terminal.
This reflects directing and questioning AI assistance, although it does not by itself
mean that I have performed the full code review expected of an experienced engineer.

## Verification evidence

Environment: Eclipse Temurin JDK 17.0.21+9, Apache Maven 3.10.0,
Spring Boot 3.5.16, and SpringDoc 2.10.0.

Command: `mvn --batch-mode --no-transfer-progress clean verify -Ddebug=false`.
Result: **BUILD SUCCESS; 9 tests run, 0 failures, 0 errors, 0 skipped**.
The tests cover application startup and H2 connectivity, initial sample counts,
the full Book CRUD lifecycle, Member creation and retrieval, missing resources,
invalid input, client-supplied IDs, Borrowing persistence, and OpenAPI/Swagger routes.

A separate process ran the executable JAR with Java 17 on localhost port 18080
for these live HTTP checks (normal application port remains 8080):

| Request | Observed result |
|---------|-----------------|
| `GET /books` | HTTP 200; 5 sample books as JSON |
| `GET /members` | HTTP 200; 3 sample members as JSON |
| `GET /h2-console/` | HTTP 200; H2 Console HTML |
| `GET /swagger-ui.html` | HTTP 200 after redirect; Swagger UI HTML |
| `GET /v3/api-docs` | HTTP 200; Book and Member routes documented |

The temporary process was stopped after verification.
The initial live-check script incorrectly counted PowerShell's wrapped JSON array;
correcting the script produced the sample counts above without application changes.
SpringDoc was selected from its [Spring Boot 3 documentation](https://springdoc.org/v2/).

Git author identity and a remote URL were not configured at initial inspection.
The student subsequently supplied `https://github.com/Yvesch12/library-api.git`
and delegated the Git setup. The submission uses the GitHub login `Yvesch12`
and the no-reply email `230360058+Yvesch12@users.noreply.github.com`, constructed
using the account's public GitHub ID, with identity configured only in this repository.
The submission branch is `main` and the lab tag is `lab-1`.

## Submission and personal review

The application and report are versioned in the course repository. The `lab-1` tag
identifies the completed Lab 1 snapshot on `main`.

Before submitting, read the four answers and verify that the opinions expressed
match your own understanding. Compare the conceptual role description with the
lecture's actual table; its exact wording cannot be verified from the assignment.
Use the walkthrough in `docs/lab1-walkthrough.md` to inspect the code and run the API.

No later-lab deliverables or personal time estimates are claimed here.

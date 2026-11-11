# Lab 1 - Introduction to AI-Assisted Engineering

Date: 2026-11-11

Status: AI-assisted draft for the student to review and personalize before submission.
The four proposed answers below use the implementation and conversation as evidence;
they do not establish that the student has reviewed or agrees with each decision.

## Observation

Codex kept the `com.example.library` root package and separated entities, repositories,
and controllers; a proposed assessment is to agree because each layer has an identifiable
responsibility and Spring can discover it under the application package.
It chose `LocalDate` for calendar dates and `BigDecimal` with a two-decimal database
column for fines; a proposed assessment is to agree because dates need no time of day
and money should avoid floating-point rounding.
It used `@ManyToOne` from Borrowing to Book and Member without reverse collections;
a proposed assessment is to agree because multiple borrowing records can refer to
the same book or member while JSON does not form an object cycle.
These decisions should be checked against the lab specification and personally accepted
or challenged before this draft is submitted.

## Engineering judgment

Accepting the first skeleton without comparing it to the assignment would have left
out all three entities, the repositories, sample data, the required endpoints, and Swagger UI.
One configuration that needed verification was `spring.jpa.defer-datasource-initialization=true`,
which makes Hibernate create tables before `data.sql` inserts the sample records.
Another observed issue was that the terminal initially could not find Maven, although
it was installed, because the terminal retained its earlier environment settings.
The student must verify the actual responses and review validation and ID handling,
since a successful build alone does not demonstrate that the assignment is satisfied.

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

The lecture's exact "Junior vs Senior in the AI era" table was not included in the
assignment, so its row must be selected by the student before submission.
The observed activities that support the answer are specifying Java 17 and Spring Boot 3,
requiring approval before setup changes, reporting a failed Maven command, and providing
the deliverable specification to guide the implementation.
These activities show the human directing and checking the work while Codex performs
implementation tasks.
Replace this draft with the exact lecture row and explain, in your own words, whether
that row describes what you actually reviewed and decided during this lab.

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

## Submission checklist

- [ ] Read the generated code and personalize all four reflection answers.
- [ ] Select and cite the correct row from the lecture table for the role reflection.
- [ ] Confirm the final commit is on `main` and has tag `lab-1`.
- [ ] Push `main` and `lab-1` to the private course repository.

No later-lab deliverables or personal time estimates are claimed here.

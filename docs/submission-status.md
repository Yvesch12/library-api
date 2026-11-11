# Submission status

## Verified technical artifacts

- Eight lab reports are present, with limitations and authorship identified.
- Lab 2 test-first commit precedes implementation; all four stories work.
- Lab 3 specification has eight rules and five acceptance criteria; tests pass.
- Lab 4 controllers use DTOs/services, shared errors, annotated OpenAPI; Dockerfile
  and CI container build/smoke workflow are present.
- Lab 5 has three refactorings and three separate 24-test passing regression runs.
- Lab 6 PMD/SpotBugs ran; actionable ErrorResponse warnings decreased from two to
  zero; raw analyzer snapshots and GQM/complexity/coupling data are committed.
- Lab 7 final clean build: 44 tests, overall line 89.94%, service line/branch 100%;
  both deliberate fine mutations detected and source restored.
- Lab 8 contains an explicit UFP inventory (79 UFP), reproducible arithmetic,
  COCOMO illustration, and qualified effort/LOC/FP comparisons.
- Final packaged application smoke check passed with Java 17 and the container's
  -Xmx256m/-Xms128m limits on localhost port 18080: five books, three members,
  Actuator health UP, Swagger UI HTTP 200, and 14 OpenAPI operations with summaries
  and error responses. The temporary process was stopped; the student's existing
  server was not modified. This is Java-process evidence, not a Docker run.

## Evidence that must not be fabricated

- Docker is absent locally. Container execution was verified on GitHub's Linux
  runner in run 43100862867: Maven verification, Docker image build, and container
  HTTP checks passed after a readiness-wait fix (commit ba4ca9b).
- Personal estimates/actual hours were never supplied. Agent wall-time measures
  and handout estimates are explicitly labelled; full personal EQF/productivity
  cannot be calculated from the available evidence.
- The student has not claimed understanding/manual review. AI-generated reports
  and tests should not be submitted as evidence of manual student activities.
- The lecture's exact Junior/Senior table is not available; Lab 1 describes a
  conceptual role without inventing a quotation.

## Final hand-in

Submit the repository link and requested lab tags via the course's submission
system. Pushing to GitHub does not submit to an external university portal.
The final Git verification checks that main and lab-8 are pushed and that all
eight progressive tags exist; no extra history is squashed merely to force eight commits.

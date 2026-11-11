# Lab 1 walkthrough

## 1. Understand a request

Open these files in order:

1. `src/main/java/com/example/library/entity/Book.java`: describes a book's fields.
   `@Entity` maps it to a table, `@Id` identifies its primary key, and
   `@GeneratedValue` lets the database assign the ID.
2. `src/main/java/com/example/library/repository/BookRepository.java`: extends
   `JpaRepository<Book, Long>`, which supplies methods such as `findAll`, `findById`,
   `save`, and `delete` without a handwritten implementation.
3. `src/main/java/com/example/library/controller/BookController.java`: maps HTTP
   requests to those repository methods. `@GetMapping` handles reads and
   `@Valid` checks request fields before creation or update.

For `GET /books`, Spring calls `getBooks()`, the repository reads H2, and Spring
serializes the resulting list into JSON. For an unknown book ID, the controller
returns 404 instead of pretending a record exists.

Look at `Borrowing.java` next: each borrowing references one Book and one Member.
The relationships are present for Lab 1, but borrowing and return workflows start
in Lab 2. A field of type `BigDecimal` stores exact decimal money values.

## 2. Check the build

From the project folder in PowerShell:

```powershell
java -version
mvn -version
mvn test
```

Java and the Java runtime listed by Maven should be 17. The test result should
show 9 tests, 0 failures, and 0 errors. Automated tests verify specific behaviors;
passing them does not mean every possible behavior is correct.

If this terminal still cannot find Maven, refresh only this session:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot'
$env:Path = "$env:JAVA_HOME\bin;C:\Users\Yves\tools\apache-maven-3.10.0\bin;$env:Path"
```

## 3. Run and inspect the application

```powershell
mvn spring-boot:run
```

Keep this terminal running and open:

- http://localhost:8080/books: a JSON array with 5 sample books.
- http://localhost:8080/members: a JSON array with 3 sample members.
- http://localhost:8080/swagger-ui.html: interactive endpoint documentation.
- http://localhost:8080/h2-console: database console.

For H2, use JDBC URL `jdbc:h2:mem:librarydb`, username `sa`, and an empty password.
Run `SELECT * FROM BOOK;` to see the same records exposed by `/books`.
`data.sql` supplies these records after Hibernate creates the tables.
The database resets on restart because it is in memory.

In Swagger UI, expand `GET /books`, select **Try it out**, and then **Execute**.
Check the 200 response and JSON body. Try `GET /books/999999` and check the 404
response. You can also create a book using the POST example in the README.

Return to the terminal and press Ctrl+C when finished.

## 4. Review your report

Read `reports/lab1-report.md`. Its four reflection sections address:

1. Three implementation decisions and reasons for agreeing with them.
2. Why the initial scaffold and terminal setup still required verification.
3. How method-level coding differs from coordinating a software lifecycle.
4. Your actual role specifying the task and questioning AI output.

The lecture's exact Junior/Senior table is unavailable in the supplied material.
Compare the report's conceptual description with the table in your slides.
Change any opinion that does not reflect what you think; do not claim a personal
review merely because an automated test passed.

## 5. Verify the submission

```powershell
git status
git log -2 --oneline
git show --stat lab-1
git ls-remote --heads --tags origin main lab-1
```

The working tree should be clean, and the remote `main` and `lab-1` should identify
the completed Lab 1 commit. Submit the repository URL or tag link according to
your instructor's submission instructions.

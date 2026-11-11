"""Build the printable Moodle evidence report from committed lab artifacts.

Render reports/reference-lab-submission.html to PDF using a browser's print-to-PDF.
No third-party Python packages are required.
"""
from html import escape
import json
from pathlib import Path
import subprocess
from xml.etree import ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
REPO = 'https://github.com/Yvesch12/library-api'
SNAPSHOT = subprocess.check_output(['git', 'rev-parse', 'lab-8'], cwd=ROOT, text=True).strip()
metrics = json.loads((ROOT / 'docs/lab7-metrics.json').read_text(encoding='utf-8-sig'))
analysis = json.loads((ROOT / 'docs/lab6-analysis-results.json').read_text(encoding='utf-8-sig'))
mutations = json.loads((ROOT / 'docs/lab7-mutations.json').read_text(encoding='utf-8-sig'))
test_suites = [ET.parse(p).getroot() for p in sorted((ROOT / 'target/surefire-reports').glob('TEST-*.xml'))]
tests = sum(int(s.get('tests', 0)) for s in test_suites)
failures = sum(int(s.get('failures', 0)) + int(s.get('errors', 0)) for s in test_suites)
if not test_suites or failures:
    raise RuntimeError('A real, passing test report is required before producing the PDF.')

def link(path, label=None):
    return '<a href="' + REPO + '/blob/' + SNAPSHOT + '/' + path + '">' + escape(label or path) + '</a>'

def page(number, title, content):
    return f'<section class="page"><header>SOFTWARE ENGINEERING / REFERENCE LAB</header><h1>{title}</h1>{content}<footer>Yvesch12 &nbsp; | &nbsp; 11 November 2026 <span>{number} / 6</span></footer></section>'

pages = []
pages.append(page(1, 'Agentic development<br>Reference Lab execution', f'''
<p class="subtitle">Moodle evidence summary &bull; Software Engineering 2026&ndash;2027</p>
<div class="identity"><b>Student account:</b> Yvesch12<br><b>Tool:</b> OpenAI Codex in the Windows / VS Code development workspace<br><b>Reference project:</b> Library Management REST API</div>
<h2>Purpose and scope</h2>
<p>This report documents the controlled Reference Lab, rather than a separate final-project idea. It summarizes how instructions, approval boundaries, generated artifacts, failures, tests, and refinements were used across the eight reference labs. It does not reproduce the full source code.</p>
<h2>Implementation and evidence location</h2>
<p><a href="{REPO}/tree/main">{REPO}/tree/main</a><br>Completed implementation checkpoint: <a href="{REPO}/tree/lab-8">tag lab-8</a>, commit <code>{SNAPSHOT[:7]}</code>.<br>Tags <code>lab-1</code> through <code>lab-8</code> preserve progressive snapshots.</p>
<p>The code was committed and pushed to this repository. This PDF is a separate submission artifact; publishing to GitHub does not submit it to Moodle.</p>
<h2>Verified result</h2>
<div class="stats"><div><b>{tests}</b>passing tests</div><div><b>{metrics['overall']['LINE']['percent']}%</b>overall line coverage</div><div><b>100%</b>service line / branch coverage</div></div>
<p>The final packaged Java application was started and checked over HTTP: five seed books, three seed members, health status UP, Swagger UI, and 14 documented operations. Static analysis and two deliberate fine-calculation bugs were also executed.</p>
<h2>Evidence and authorship boundaries</h2>
<p>The student specified constraints, approved environment setup, reported a Maven failure, supplied the assignment, ran the application, and shared database/Swagger results. Later implementation, tests, analysis, and report preparation were delegated to Codex. These artifacts demonstrate the recorded process; they are not proof of unobserved manual review or independent student mastery.</p>
<p class="note">Docker was unavailable locally. GitHub Actions remained queued at the report check; a successful container run is not claimed. Personal study hours and the exact lecture table were not supplied.</p>
'''))
pages.append(page(2, 'Interactions and decisions', '''
<p>The examples below are excerpts from the actual conversation. Agent responses are summarized unless explicitly presented as terminal output.</p>
<h2>1. Specify scope and restrict changes</h2>
<blockquote>“First inspect the workspace and verify the development environment. Then propose steps to configure Java 17 and install Maven.”<br>“Do not modify files, install software, or change environment variables without my approval”</blockquote>
<p><b>Agent action:</b> read-only inspection found an empty Git repository, Java 24 selected in the process, working Temurin 17, and no Maven command. A setup proposal was returned before installation.</p>
<h2>2. Approve concrete setup work</h2>
<blockquote>“I approve the proposed environment setup.”<br>“Download Apache Maven from the official Apache distribution, verify its SHA-512 checksum, and install it in my user directory.”</blockquote>
<p><b>Agent action:</b> installed Maven in the user directory, verified the SHA-512 digest, and updated user settings. System settings were not changed and Java 24 was retained.</p>
<h2>3. Challenge an apparent success</h2>
<blockquote>“mvn didnt work”</blockquote>
<p>The supplied error said <code>mvn</code> was not recognized. The agent compared the saved user PATH with the terminal's inherited environment, then provided a session refresh. The student subsequently supplied output showing Maven 3.10.0 running on Java 17.0.21.</p>
<h2>4. Guide the implementation with the handout</h2>
<blockquote>“okay this is the deliverables, continue”</blockquote>
<p>The initial skeleton was compared with the actual assignment. Missing entities, repositories, Book/Member routes, seed SQL, and Swagger were added. Later, “just finish everything” authorized the remaining labs; this was delegation, not evidence that every generated file had been personally reviewed.</p>
<h2>5. Validate observable behavior</h2>
<p>The student ran <code>mvn spring-boot:run</code> and shared H2 and Swagger screenshots. The H2 view showed five book rows. Swagger showed <code>GET /books/1</code> returning 200 and an unknown ID returning 404 with <code>Book not found</code>. Codex then explained the controller-to-database request flow.</p>
<p class="note">This report summarizes screenshots shown in the conversation; it does not label reconstructed text as an original screenshot. A later read-only CI credential check required explicit permission after automatic review rejected the first attempt.</p>
'''))
pages.append(page(3, 'Workflow and produced artifacts', f'''
<p>The sequence followed inspection &rarr; proposal/authorization &rarr; edits &rarr; execution &rarr; review &rarr; refinement &rarr; versioned evidence.</p>
<table><thead><tr><th>Lab / SDLC activity</th><th>Outputs and observable evidence</th></tr></thead><tbody>
<tr><td>1 &bull; Setup and scaffold</td><td>Java 17 / Maven setup; three JPA entities; repositories; Book and Member CRUD; five books / three members; nine passing tests; lab-1 tag.</td></tr>
<tr><td>2 &bull; Planning and TDD</td><td>Sprint backlog; borrowing, return, active loans and fines; red compile failure before service implementation; 15 passing tests; pair-review findings.</td></tr>
<tr><td>3 &bull; Specification</td><td>docs/AGENTS.md with eight rules and five acceptance criteria; reservation service, routes and tests; 21 passing tests.</td></tr>
<tr><td>4 &bull; Design and integration</td><td>DTOs/mappers, application services, shared error handler, annotated OpenAPI, multi-stage Dockerfile and CI workflow; 24 passing tests.</td></tr>
<tr><td>5 &bull; Refactoring</td><td>Extract day computation; name fine/loan constants; centralize availability updates; three separate 24-test regression runs; before/after diff.</td></tr>
<tr><td>6 &bull; Quality measurement</td><td>PMD, SpotBugs, JaCoCo; GQM plan; complexity/coupling tables; mutable error-map fix; 25 passing tests.</td></tr>
<tr><td>7 &bull; Test evaluation</td><td>Mockito audit; real HTTP flows; fine mutations; 44 passing tests; final coverage 89.94% overall and 100% service line/branch.</td></tr>
<tr><td>8 &bull; Estimation</td><td>79-UFP inventory; three-point size and COCOMO calculations; qualified LOC/FP analysis and measured agent-session time.</td></tr>
</tbody></table>
<h2>Representative artifacts</h2>
<ul><li>{link('docs/AGENTS.md')} &mdash; reservation requirements and acceptance criteria.</li>
<li>{link('docs/sprint-backlog.md')} &mdash; planning estimates and timing provenance.</li>
<li>{link('docs/lab5-refactor.diff')} &mdash; exact behavior-preserving changes.</li>
<li>{link('docs/gqm.md')} &mdash; measurement goal, questions, metrics, results.</li>
<li>{link('docs/lab7-test-audit.md')} and {link('docs/lab7-mutations.json')}.</li>
<li>{link('Dockerfile')} and {link('.github/workflows/verify.yml')}.</li>
<li>Eight detailed reports under <a href="{REPO}/tree/{SNAPSHOT}/reports">reports/</a>.</li></ul>
<h2>Architecture</h2>
<div class="flow">HTTP JSON &rarr; DTO / controller &rarr; service &rarr; repository &rarr; H2</div>
<p>Controllers handle transport, services coordinate business rules and transactions, repositories provide persistence, and entities map stored data. DTOs keep the public contract independent of JPA relationship details.</p>
'''))
suite_rows = ''.join(f'<tr><td>{escape(s.get("name", "").split(".")[-1])}</td><td>{s.get("tests")}</td><td>{s.get("failures")}/{s.get("errors")}</td></tr>' for s in test_suites)
pages.append(page(4, 'Execution and test evidence', f'''
<h2>Student-supplied application startup</h2>
<p>Extracted from the terminal output supplied in the session (11 November 2026, Asia/Beirut):</p>
<pre>Java version: 17.0.21, vendor: Eclipse Adoptium
Apache Maven 3.10.0
Tomcat started on port 8080 (http)
Started LibraryApiApplication in 3.331 seconds
H2 console available at '/h2-console'
Database available at 'jdbc:h2:mem:librarydb'</pre>
<h2>Student-supplied endpoint evidence</h2>
<p>Transcribed from the Swagger screenshots:</p>
<table><tr><th>Request</th><th>Visible response</th></tr><tr><td>GET /books/1</td><td>200; id 1, title Clean Code, author Robert C. Martin, publishedYear 2008, available true.</td></tr><tr><td>GET /books/999999</td><td>404; title Not Found; detail Book not found; instance /books/999999.</td></tr></table>
<h2>Final automated test run</h2>
<pre>mvn --batch-mode --no-transfer-progress clean verify -Ddebug=false
Tests run: {tests}, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
Total time: 19.391 s</pre>
<table class="compact"><tr><th>Suite (from Surefire XML)</th><th>Tests</th><th>Failures/errors</th></tr>{suite_rows}</table>
<p>The HTTP flow suite starts a real server on a random port and checks borrowing, a full return cycle, and duplicate-loan conflict. MockMvc checks cover validation, missing resources, CRUD, reservation criteria and OpenAPI. Mockito tests isolate BorrowingService without a Spring context.</p>
'''))
pages.append(page(5, 'Review, problems and refinement', f'''
<table><tr><th>Observed problem</th><th>Action and validation</th></tr>
<tr><td>Java 24 selected / Maven unavailable</td><td>Use JDK 17, install verified Maven ZIP, configure user environment; refreshed terminal reports Java 17 and Maven 3.10.0.</td></tr>
<tr><td>PowerShell download TLS decryption error</td><td>Retried using Windows curl with HTTPS verification enabled; SHA-512 matched before extraction.</td></tr>
<tr><td>Initial scaffold incomplete for the handout</td><td>Compared against specified fields/endpoints and added missing model, seed data, routes and documentation.</td></tr>
<tr><td>Inconsistent validation response shape</td><td>Made custom advice take precedence over Spring Boot's default problem-details advice; handler integration tests passed.</td></tr>
<tr><td>Mutable field-error map exposed</td><td>SpotBugs identified representation exposure; Map.copyOf plus an immutability test removed both targeted findings.</td></tr>
<tr><td>HTTP test client discarded generated IDs</td><td>Removed Jackson read-only ID binding from shared DTOs; retained OpenAPI read-only metadata and service-side ID protection; real HTTP tests passed.</td></tr>
</table>
<h2>Measured quality, with remaining findings</h2>
<table><tr><th>Metric</th><th>Before</th><th>After</th></tr>
<tr><td>Overall line coverage</td><td>75.79%</td><td>89.94%</td></tr>
<tr><td>Service line / branch coverage</td><td>100% / 94.44%</td><td>100% / 100%</td></tr>
<tr><td>SpotBugs findings</td><td>{analysis['spotbugsBefore']}</td><td>{analysis['spotbugsAfter']} (targeted map warnings: 0)</td></tr>
<tr><td>PMD findings</td><td>15</td><td>{analysis['pmdAfter']}</td></tr></table>
<p>Remaining analyzer findings were documented rather than suppressed to claim a clean report. JPA relationship-exposure warnings need interpretation; blindly copying managed entities would change persistence semantics.</p>
<h2>Do the assertions detect wrong behavior?</h2>
<p>After a passing baseline, the fine rate was temporarily changed from 0.50 to 1.00: <b>{len(mutations['double-rate']['failed'])} tests failed</b>. Returning null for an on-time fine caused <b>{len(mutations['null-on-time']['failed'])} tests to fail</b>. Both original mutations were restored, and the final clean build passed. Exact failed/passed test names are preserved in {link('docs/lab7-mutations.json', 'the mutation evidence')}.</p>
<p class="note">Coverage indicates execution, not correctness. The deliberate bugs test whether specific assertions and database invariants protect the stated fine rules.</p>
'''))
pages.append(page(6, 'Reproduce, review and submit', f'''
<h2>Run the reference application</h2>
<pre>git clone {REPO}.git
cd library-api
git checkout lab-8
java -version
mvn -version
mvn clean verify
mvn spring-boot:run</pre>
<p>Use JDK 17 and Maven 3.x. Keep the server terminal open. The normal port is 8080; stop it with Ctrl+C. A previously started server must be restarted to load the latest code.</p>
<table><tr><th>URL</th><th>Expected observation</th></tr>
<tr><td>http://localhost:8080/books</td><td>Five sample books on a fresh start</td></tr>
<tr><td>http://localhost:8080/members</td><td>Three sample members on a fresh start</td></tr>
<tr><td>http://localhost:8080/swagger-ui.html</td><td>Interactive API operations</td></tr>
<tr><td>http://localhost:8080/api-docs</td><td>OpenAPI JSON (path changed in Lab 4)</td></tr>
<tr><td>http://localhost:8080/actuator/health</td><td>status UP</td></tr>
<tr><td>http://localhost:8080/h2-console</td><td>JDBC jdbc:h2:mem:librarydb; user sa; blank password</td></tr></table>
<h2>Inspect the supporting evidence</h2>
<p>Run <code>mvn pmd:pmd spotbugs:spotbugs pmd:check</code> for analyzer XML. JaCoCo HTML is produced by <code>mvn test</code> at <code>target/site/jacoco/index.html</code>. Committed XML snapshots and JSON summaries preserve measured results without adding build output to Git.</p>
<p>Test-first history: commit <a href="{REPO}/commit/de6e233">de6e233</a> contains borrowing tests before BorrowingService. Progressive tags are available on the repository's <a href="{REPO}/tags">tags page</a>. Detailed limitations: {link('docs/submission-status.md')}.</p>
<h2>What remains unverified</h2>
<p>The <a href="{REPO}/actions/runs/43098674544">GitHub container workflow</a> was queued at the latest check, so successful Docker execution is pending. Docker was not installed locally. The final Java-process smoke test is not presented as a container test.</p>
<p>No personal study-time history or manual student test authorship is invented. Later lab reports are AI-assisted execution records. Reading and explaining the artifacts is still necessary to demonstrate the student's understanding.</p>
<h2>Submission</h2>
<p>Review this PDF, then upload <b>reference-lab-submission.pdf</b> to Moodle. The implementation is already in the linked repository. Moodle upload has not been performed by Codex. This reference exercise is separate from the later final project based on the student's own idea.</p>
'''))

css = '''
@page { size: A4; margin: 0; }
* { box-sizing: border-box; }
body { margin: 0; color: #18283a; font: 10pt/1.39 Arial, sans-serif; }
.page { width: 210mm; height: 297mm; padding: 15mm 17mm 16mm; position: relative; break-after: page; overflow: visible; }
.page:last-child { break-after: auto; }
header { font-size: 8pt; font-weight: bold; letter-spacing: 1.6px; color: #246a87; border-bottom: 1px solid #c7dce4; padding-bottom: 3mm; margin-bottom: 5mm; }
h1 { font-size: 24pt; line-height: 1.15; margin: 0 0 5mm; color: #123b51; }
h2 { font-size: 12pt; margin: 4mm 0 2mm; color: #165b75; }
p { margin: 0 0 2.7mm; }
.subtitle { color: #526777; font-size: 11pt; }
.identity { background: #edf5f8; border-left: 3px solid #287f9c; padding: 3mm 4mm; margin: 4mm 0; }
.stats { display: flex; gap: 3mm; margin: 3mm 0; }
.stats div { flex: 1; padding: 3mm; background: #edf5f8; font-size: 8.5pt; }
.stats b { display: block; font-size: 22pt; color: #165b75; }
table { width: 100%; border-collapse: collapse; margin: 2mm 0 3mm; font-size: 9pt; }
td, th { text-align: left; vertical-align: top; border-bottom: 1px solid #d4e0e5; padding: 2mm 2.2mm; }
th { background: #edf5f8; color: #123b51; }
.compact { font-size: 8.6pt; } .compact td, .compact th { padding: 1.1mm 2mm; }
blockquote { margin: 2mm 0; padding: 2mm 3mm; border-left: 2px solid #448fa8; background: #f2f6f8; font-size: 9.4pt; }
pre { white-space: pre-wrap; overflow-wrap: anywhere; font: 8.4pt/1.35 Consolas, monospace; background: #eff3f6; padding: 3mm; margin: 2mm 0 3mm; }
code { font: 8.7pt Consolas, monospace; overflow-wrap: anywhere; }
ul { padding-left: 5mm; margin: 2mm 0; } li { margin-bottom: 1.6mm; }
a { color: #155d80; text-decoration: underline; overflow-wrap: anywhere; }
.note { font-size: 9pt; color: #526777; border-left: 2px solid #bacbd3; padding-left: 3mm; }
.flow { text-align: center; padding: 3mm; background: #edf5f8; font-weight: bold; margin: 2mm 0; }
footer { position: absolute; bottom: 9mm; left: 17mm; right: 17mm; border-top: 1px solid #c7dce4; padding-top: 2mm; color: #526777; font-size: 8pt; }
footer span { float: right; }
'''
layout_script = '''<script>
const preview = Number(new URLSearchParams(location.search).get('preview'));
if (preview) document.querySelectorAll('.page').forEach((page, i) => { if (i + 1 !== preview) page.style.display = 'none'; });
document.querySelectorAll('.page').forEach((page, i) => {
  if (page.style.display === 'none') return;
  const footer = page.querySelector('footer');
  page.dataset.contentBottom = footer.previousElementSibling.getBoundingClientRect().bottom.toFixed(1);
  page.dataset.footerTop = footer.getBoundingClientRect().top.toFixed(1);
});
</script>'''
document = '<!doctype html><html lang="en"><head><meta charset="utf-8"><title>Reference Lab - Agentic Development Evidence - Yvesch12</title><style>' + css + '</style></head><body>' + ''.join(pages) + layout_script + '</body></html>'
destination = ROOT / 'reports/reference-lab-submission.html'
destination.write_text(document, encoding='utf-8')
print('Created ' + str(destination))
print(f'Evidence: {tests} tests, {failures} failures/errors; snapshot {SNAPSHOT[:7]}; 6 designed pages.')

# Borrowing review

1. IMPORTANT: reject inactive members; otherwise revoked memberships can borrow.
   Applied with a guard and integration test.
2. IMPORTANT: reject repeated returns; otherwise a later request can change the fine
   and make a book available while another member holds it. Applied with a guard/test.
3. IMPORTANT: check missing IDs and provide 404/409 rather than uncaught 500 responses.
   Applied using local handlers pending the shared handler in Lab 4.
4. IMPORTANT: concurrent borrowing requests can race on availability. Deferred to
   an explicit locking change with a concurrent regression test; single-thread
   happy-path tests are insufficient evidence for concurrent correctness.
5. MINOR: inject a clock to make date tests independent of midnight. Deferred: the
   pure fine method already accepts dates; clock injection is a useful future change.
6. XP process: all story tests were committed and run before implementation. These
   were AI-authored tests, not manually authored student tests. The red run failed
   compilation because BorrowingService did not exist, as the assignment predicts.

Rejected for this sprint: introducing event sourcing or microservices would add
operational complexity without addressing the four small user stories.

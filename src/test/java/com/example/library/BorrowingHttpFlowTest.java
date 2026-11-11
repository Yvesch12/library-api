package com.example.library;
import com.example.library.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.datasource.url=jdbc:h2:mem:httpflows")
class BorrowingHttpFlowTest {
    @Autowired private TestRestTemplate rest;
    private long[] createBookAndMember() {
        BookDTO book = new BookDTO();
        book.setIsbn(UUID.randomUUID().toString()); book.setTitle("HTTP test");
        book.setAuthor("Test Author"); book.setGenre("Engineering"); book.setPublishedYear(2020);
        ResponseEntity<BookDTO> bookResponse = rest.postForEntity("/books", book, BookDTO.class);
        assertEquals(HttpStatus.CREATED, bookResponse.getStatusCode());
        MemberDTO member = new MemberDTO(); member.setName("HTTP member"); member.setEmail("http@usj.edu.lb");
        ResponseEntity<MemberDTO> memberResponse = rest.postForEntity("/members", member, MemberDTO.class);
        assertEquals(HttpStatus.CREATED, memberResponse.getStatusCode());
        assertNotNull(bookResponse.getBody()); assertNotNull(memberResponse.getBody());
        return new long[]{bookResponse.getBody().getId(), memberResponse.getBody().getId()};
    }
    private ResponseEntity<BorrowingDTO> borrow(long[] ids) {
        return rest.postForEntity("/borrowings", Map.of("bookId", ids[0], "memberId", ids[1]), BorrowingDTO.class);
    }
    @Test void postBookMemberBorrowingChangesAvailability() {
        long[] ids = createBookAndMember();
        ResponseEntity<BorrowingDTO> response = borrow(ids);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody()); assertNotNull(response.getBody().getId());
        assertEquals(ids[0], response.getBody().getBookId());
        assertEquals(ids[1], response.getBody().getMemberId());
        assertFalse(rest.getForObject("/books/" + ids[0], BookDTO.class).isAvailable());
        BorrowingDTO[] active = rest.getForObject("/members/" + ids[1] + "/borrowings", BorrowingDTO[].class);
        assertNotNull(active); assertEquals(1, active.length);
        assertEquals(response.getBody().getId(), active[0].getId());
    }
    @Test void fullReturnCycleRestoresAvailability() {
        long[] ids = createBookAndMember();
        BorrowingDTO created = borrow(ids).getBody(); assertNotNull(created);
        ResponseEntity<BorrowingDTO> returned = rest.exchange("/borrowings/" + created.getId() + "/return",
                HttpMethod.PUT, HttpEntity.EMPTY, BorrowingDTO.class);
        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertNotNull(returned.getBody()); assertNotNull(returned.getBody().getReturnDate());
        assertEquals(0, new java.math.BigDecimal("0.00").compareTo(returned.getBody().getFineAmount()));
        assertTrue(rest.getForObject("/books/" + ids[0], BookDTO.class).isAvailable());
        BorrowingDTO[] active = rest.getForObject("/members/" + ids[1] + "/borrowings", BorrowingDTO[].class);
        assertNotNull(active); assertEquals(0, active.length);
    }
    @Test void secondBorrowReturns409AndKeepsUnavailable() {
        long[] ids = createBookAndMember();
        assertEquals(HttpStatus.CREATED, borrow(ids).getStatusCode());
        ResponseEntity<Map> response = rest.postForEntity("/borrowings",
                Map.of("bookId", ids[0], "memberId", ids[1]), Map.class);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Book is already borrowed", response.getBody().get("error"));
        assertFalse(rest.getForObject("/books/" + ids[0], BookDTO.class).isAvailable());
    }
}

package com.example.library.service;
import com.example.library.entity.*;
import com.example.library.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @Transactional
class ReservationServiceTest {
    @Autowired private ReservationService service;
    @Autowired private BookRepository books;
    @Autowired private MemberRepository members;
    @Autowired private ReservationRepository reservations;
    private void unavailable() { Book b = books.findById(1L).orElseThrow(); b.setAvailable(false); books.save(b); }
    @Test void ac1AvailableBookRejected() {
        assertEquals("Book is available for direct borrowing.", assertThrows(IllegalStateException.class,
                () -> service.reserve(1L, 1L)).getMessage());
    }
    @Test void ac2CreatesActiveWithoutChangingAvailability() {
        unavailable(); Reservation r = service.reserve(1L, 1L);
        assertNotNull(r.getId()); assertEquals(LocalDate.now(), r.getReservedDate());
        assertEquals(ReservationStatus.ACTIVE, r.getStatus());
        assertFalse(books.findById(1L).orElseThrow().isAvailable());
    }
    @Test void ac3DuplicateRejected() {
        unavailable(); service.reserve(1L, 1L);
        assertThrows(IllegalStateException.class, () -> service.reserve(1L, 1L));
        assertEquals(1, reservations.count());
    }
    @Test void ac4CancellationListingAndRereservation() {
        unavailable(); Reservation r = service.reserve(1L, 1L);
        assertEquals(ReservationStatus.CANCELLED, service.cancel(r.getId()).getStatus());
        assertEquals(r.getId(), service.list(1L).get(0).getId());
        assertThrows(IllegalStateException.class, () -> service.cancel(r.getId()));
        assertNotEquals(r.getId(), service.reserve(1L, 1L).getId());
    }
    @Test void ac5MissingAndInactive() {
        assertThrows(NoSuchElementException.class, () -> service.reserve(999L, 1L));
        assertThrows(NoSuchElementException.class, () -> service.reserve(1L, 999L));
        assertThrows(NoSuchElementException.class, () -> service.cancel(999L));
        assertThrows(NoSuchElementException.class, () -> service.list(999L));
        unavailable(); Member m = members.findById(1L).orElseThrow(); m.setActive(false);
        assertThrows(IllegalStateException.class, () -> service.reserve(1L, 1L));
        assertTrue(service.list(2L).isEmpty());
    }
}

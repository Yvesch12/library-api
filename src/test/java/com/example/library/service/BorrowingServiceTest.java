package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.entity.Borrowing;
import com.example.library.entity.Member;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BorrowingServiceTest {
    @Autowired private BorrowingService service;
    @Autowired private BookRepository books;
    @Autowired private MemberRepository members;
    @Autowired private BorrowingRepository borrowings;

    @Test void borrowBookMarksUnavailableAndSetsFourteenDayDeadline() {
        Borrowing result = service.borrowBook(1L, 1L);
        assertNotNull(result.getId());
        assertFalse(books.findById(1L).orElseThrow().isAvailable());
        assertEquals(LocalDate.now(), result.getBorrowDate());
        assertEquals(LocalDate.now().plusDays(14), result.getDueDate());
    }
    @Test void cannotBorrowTwice() {
        service.borrowBook(1L, 1L);
        assertThrows(IllegalStateException.class, () -> service.borrowBook(1L, 2L));
        assertEquals(1, borrowings.count());
    }
    @Test void returnBookMakesAvailableAndChargesFine() {
        Borrowing loan = service.borrowBook(1L, 1L);
        loan.setDueDate(LocalDate.now().minusDays(3));
        borrowings.saveAndFlush(loan);
        Borrowing result = service.returnBook(loan.getId());
        assertEquals(LocalDate.now(), result.getReturnDate());
        assertEquals(new BigDecimal("1.50"), result.getFineAmount());
        assertTrue(books.findById(1L).orElseThrow().isAvailable());
    }
    @Test void activeBorrowingsExcludeReturnedAndOtherMembers() {
        Borrowing returned = service.borrowBook(1L, 1L);
        service.returnBook(returned.getId());
        Borrowing active = service.borrowBook(2L, 1L);
        service.borrowBook(3L, 2L);
        var result = service.activeBorrowings(1L);
        assertEquals(1, result.size());
        assertEquals(active.getId(), result.get(0).getId());
    }
    @Test void fineBoundaryCases() {
        LocalDate due = LocalDate.of(2026, 1, 10);
        assertEquals(new BigDecimal("0.00"), service.calculateFine(due, due.minusDays(1)));
        assertEquals(new BigDecimal("0.00"), service.calculateFine(due, due));
        assertEquals(new BigDecimal("0.50"), service.calculateFine(due, due.plusDays(1)));
        assertEquals(new BigDecimal("3.50"), service.calculateFine(due, due.plusDays(7)));
    }
    @Test void inactiveMemberAndRepeatedReturnAreRejected() {
        Member member = members.findById(1L).orElseThrow();
        member.setActive(false);
        assertThrows(IllegalStateException.class, () -> service.borrowBook(1L, 1L));
        member.setActive(true);
        Borrowing loan = service.borrowBook(1L, 1L);
        service.returnBook(loan.getId());
        assertThrows(IllegalStateException.class, () -> service.returnBook(loan.getId()));
    }
}

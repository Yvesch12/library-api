package com.example.library.service;
import com.example.library.entity.*;
import com.example.library.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceUnitTest {
    @Mock private BookRepository books;
    @Mock private MemberRepository members;
    @Mock private BorrowingRepository borrowings;
    @InjectMocks private BorrowingService service;

    private Book available() {
        return new Book(1L, "978-0-13-235088-4", "Clean Code", "Robert C. Martin", "Engineering", 2008, true);
    }
    private Member active() { return new Member(2L, "Alice", "alice@usj.edu.lb", LocalDate.now(), true); }
    private Borrowing loan(LocalDate due) {
        return new Borrowing(3L, available(), active(), due.minusDays(14), due, null, new BigDecimal("0.00"));
    }
    @Test void borrowHappyPathPersistsAvailabilityAndDeadline() {
        Book book = available();
        when(books.findById(1L)).thenReturn(Optional.of(book));
        when(members.findById(2L)).thenReturn(Optional.of(active()));
        when(borrowings.save(any())).thenAnswer(call -> {
            Borrowing result = call.getArgument(0); result.setId(3L); return result;
        });
        Borrowing result = service.borrowBook(1L, 2L);
        assertEquals(3L, result.getId());
        assertFalse(book.isAvailable());
        assertEquals(LocalDate.now(), result.getBorrowDate());
        assertEquals(LocalDate.now().plusDays(14), result.getDueDate());
        assertEquals(1L, result.getBook().getId());
        assertEquals(2L, result.getMember().getId());
        assertNull(result.getReturnDate());
        assertEquals(new BigDecimal("0.00"), result.getFineAmount());
        verify(books).save(book);
        verify(borrowings).save(result);
    }
    @Test void missingBookDoesNotWrite() {
        when(books.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.borrowBook(1L, 2L));
        verifyNoInteractions(members, borrowings);
        verify(books, never()).save(any());
    }
    @Test void missingMemberDoesNotChangeAvailability() {
        Book book = available();
        when(books.findById(1L)).thenReturn(Optional.of(book));
        when(members.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.borrowBook(1L, 2L));
        assertTrue(book.isAvailable());
        verify(books, never()).save(any());
        verifyNoInteractions(borrowings);
    }
    @Test void alreadyBorrowedDoesNotWrite() {
        Book book = available(); book.setAvailable(false);
        when(books.findById(1L)).thenReturn(Optional.of(book));
        when(members.findById(2L)).thenReturn(Optional.of(active()));
        assertThrows(IllegalStateException.class, () -> service.borrowBook(1L, 2L));
        verifyNoInteractions(borrowings);
        verify(books, never()).save(any());
        assertFalse(book.isAvailable());
    }
    @Test void inactiveMemberDoesNotWrite() {
        Book book = available(); Member member = active(); member.setActive(false);
        when(books.findById(1L)).thenReturn(Optional.of(book));
        when(members.findById(2L)).thenReturn(Optional.of(member));
        assertThrows(IllegalStateException.class, () -> service.borrowBook(1L, 2L));
        assertTrue(book.isAvailable());
        verifyNoInteractions(borrowings);
        verify(books, never()).save(any());
    }
    @Test void returnOnTimeHasZeroFineAndRestoresBook() {
        Borrowing loan = loan(LocalDate.now().plusDays(1)); loan.getBook().setAvailable(false);
        when(borrowings.findById(3L)).thenReturn(Optional.of(loan));
        when(borrowings.save(loan)).thenReturn(loan);
        Borrowing result = service.returnBook(3L);
        assertEquals(LocalDate.now(), result.getReturnDate());
        assertEquals(new BigDecimal("0.00"), result.getFineAmount());
        assertTrue(result.getBook().isAvailable());
        verify(books).save(loan.getBook());
        verify(borrowings).save(loan);
    }
    @Test void returnThreeDaysLateChargesOneFifty() {
        Borrowing loan = loan(LocalDate.now().minusDays(3));
        when(borrowings.findById(3L)).thenReturn(Optional.of(loan));
        when(borrowings.save(loan)).thenReturn(loan);
        assertEquals(new BigDecimal("1.50"), service.returnBook(3L).getFineAmount());
    }
    @Test void returnExactlyOnDueDateHasZeroFine() {
        Borrowing loan = loan(LocalDate.now());
        when(borrowings.findById(3L)).thenReturn(Optional.of(loan));
        when(borrowings.save(loan)).thenReturn(loan);
        assertEquals(new BigDecimal("0.00"), service.returnBook(3L).getFineAmount());
    }
    @Test void missingBorrowingDoesNotWrite() {
        when(borrowings.findById(3L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.returnBook(3L));
        verifyNoInteractions(books);
        verify(borrowings, never()).save(any());
    }
    @Test void repeatedReturnPreservesOriginalDateAndFine() {
        Borrowing loan = loan(LocalDate.now().minusDays(4));
        LocalDate returned = LocalDate.now().minusDays(2);
        loan.setReturnDate(returned); loan.setFineAmount(new BigDecimal("1.00"));
        when(borrowings.findById(3L)).thenReturn(Optional.of(loan));
        assertThrows(IllegalStateException.class, () -> service.returnBook(3L));
        assertEquals(returned, loan.getReturnDate());
        assertEquals(new BigDecimal("1.00"), loan.getFineAmount());
        verifyNoInteractions(books);
        verify(borrowings, never()).save(any());
    }
    @Test void activeLoansUseMemberFilter() {
        when(members.existsById(2L)).thenReturn(true);
        Borrowing loan = loan(LocalDate.now());
        when(borrowings.findByMemberIdAndReturnDateIsNull(2L)).thenReturn(List.of(loan));
        assertEquals(List.of(loan), service.activeBorrowings(2L));
    }
    @Test void missingMemberCannotListLoans() {
        when(members.existsById(2L)).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> service.activeBorrowings(2L));
        verifyNoInteractions(borrowings);
    }
    @ParameterizedTest
    @CsvSource({"-1,0.00", "0,0.00", "1,0.50", "7,3.50"})
    void fineBoundariesUseExactDecimalAmounts(int days, String expected) {
        LocalDate due = LocalDate.of(2026, 1, 10);
        assertEquals(new BigDecimal(expected), service.calculateFine(due, due.plusDays(days)));
    }
}

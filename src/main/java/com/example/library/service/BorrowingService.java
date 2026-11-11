package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.entity.Member;
import com.example.library.entity.Borrowing;
import com.example.library.repository.BookRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.BorrowingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BorrowingService {
    private static final BigDecimal DAILY_FINE_RATE = new BigDecimal("0.50");
    private static final int DEFAULT_LOAN_DAYS = 14;
    private final BookRepository books;
    private final MemberRepository members;
    private final BorrowingRepository borrowings;

    public BorrowingService(BookRepository books, MemberRepository members, BorrowingRepository borrowings) {
        this.books = books;
        this.members = members;
        this.borrowings = borrowings;
    }

    public Borrowing borrowBook(Long bookId, Long memberId) {
        Book book = books.findById(bookId).orElseThrow(() -> new NoSuchElementException("Book not found"));
        Member member = members.findById(memberId).orElseThrow(() -> new NoSuchElementException("Member not found"));
        if (!book.isAvailable()) { throw new IllegalStateException("Book is already borrowed"); }
        if (!member.isActive()) { throw new IllegalStateException("Member is inactive"); }
        markAvailable(book, false);
        LocalDate today = LocalDate.now();
        return borrowings.save(new Borrowing(null, book, member, today, today.plusDays(DEFAULT_LOAN_DAYS),
                null, new BigDecimal("0.00")));
    }

    public Borrowing returnBook(Long borrowingId) {
        Borrowing loan = borrowings.findById(borrowingId)
                .orElseThrow(() -> new NoSuchElementException("Borrowing not found"));
        if (loan.getReturnDate() != null) { throw new IllegalStateException("Borrowing already returned"); }
        LocalDate today = LocalDate.now();
        loan.setReturnDate(today);
        loan.setFineAmount(calculateFine(loan.getDueDate(), today));
        markAvailable(loan.getBook(), true);
        return borrowings.save(loan);
    }

    @Transactional(readOnly = true)
    public List<Borrowing> activeBorrowings(Long memberId) {
        if (!members.existsById(memberId)) { throw new NoSuchElementException("Member not found"); }
        return borrowings.findByMemberIdAndReturnDateIsNull(memberId);
    }

    public BigDecimal calculateFine(LocalDate dueDate, LocalDate returnDate) {
        return DAILY_FINE_RATE.multiply(BigDecimal.valueOf(daysOverdue(dueDate, returnDate)));
    }

    private long daysOverdue(LocalDate dueDate, LocalDate returnDate) {
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, returnDate));
    }

    private void markAvailable(Book book, boolean available) {
        book.setAvailable(available);
        books.save(book);
    }
}

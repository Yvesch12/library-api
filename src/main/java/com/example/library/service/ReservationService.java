package com.example.library.service;
import com.example.library.entity.*;
import com.example.library.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ReservationService {
    private final BookRepository books;
    private final MemberRepository members;
    private final ReservationRepository reservations;
    public ReservationService(BookRepository books, MemberRepository members, ReservationRepository reservations) {
        this.books = books; this.members = members; this.reservations = reservations;
    }
    public Reservation reserve(Long bookId, Long memberId) {
        Book book = books.findById(bookId).orElseThrow(() -> new NoSuchElementException("Book not found"));
        Member member = members.findById(memberId).orElseThrow(() -> new NoSuchElementException("Member not found"));
        if (book.isAvailable()) { throw new IllegalStateException("Book is available for direct borrowing."); }
        if (!member.isActive()) { throw new IllegalStateException("Member is inactive"); }
        if (reservations.existsByBookIdAndMemberIdAndStatus(bookId, memberId, ReservationStatus.ACTIVE)) {
            throw new IllegalStateException("Reservation already exists");
        }
        Reservation result = new Reservation();
        result.setBook(book); result.setMember(member); result.setReservedDate(LocalDate.now());
        result.setStatus(ReservationStatus.ACTIVE);
        return reservations.save(result);
    }
    public Reservation cancel(Long id) {
        Reservation result = reservations.findById(id).orElseThrow(() -> new NoSuchElementException("Reservation not found"));
        if (result.getStatus() != ReservationStatus.ACTIVE) { throw new IllegalStateException("Reservation already cancelled"); }
        result.setStatus(ReservationStatus.CANCELLED);
        return reservations.save(result);
    }
    @Transactional(readOnly = true)
    public List<Reservation> list(Long memberId) {
        if (!members.existsById(memberId)) { throw new NoSuchElementException("Member not found"); }
        return reservations.findByMemberIdOrderByIdAsc(memberId);
    }
}

package com.example.library.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) private Book book;
    @ManyToOne(optional = false) private Member member;
    @Column(nullable = false) private LocalDate reservedDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ReservationStatus status;
    public Reservation() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public LocalDate getReservedDate() { return reservedDate; }
    public void setReservedDate(LocalDate reservedDate) { this.reservedDate = reservedDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}

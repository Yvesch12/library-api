package com.example.library.mapper;
import com.example.library.dto.BorrowingDTO;
import com.example.library.entity.*;
import org.springframework.stereotype.Component;

@Component
public class BorrowingMapper {
    public BorrowingDTO toDTO(Borrowing entity) {
        BorrowingDTO dto = new BorrowingDTO();
        dto.setId(entity.getId());
        dto.setBookId(entity.getBook().getId());
        dto.setMemberId(entity.getMember().getId());
        dto.setBorrowDate(entity.getBorrowDate());
        dto.setDueDate(entity.getDueDate());
        dto.setReturnDate(entity.getReturnDate());
        dto.setFineAmount(entity.getFineAmount());
        return dto;
    }
    public Borrowing toEntity(BorrowingDTO dto) {
        Borrowing entity = new Borrowing();
        entity.setId(dto.getId());
        Book bookIdReference = new Book();
        bookIdReference.setId(dto.getBookId());
        entity.setBook(bookIdReference);
        Member memberIdReference = new Member();
        memberIdReference.setId(dto.getMemberId());
        entity.setMember(memberIdReference);
        entity.setBorrowDate(dto.getBorrowDate());
        entity.setDueDate(dto.getDueDate());
        entity.setReturnDate(dto.getReturnDate());
        entity.setFineAmount(dto.getFineAmount());
        return entity;
    }
}

package com.example.library.mapper;
import com.example.library.dto.ReservationDTO;
import com.example.library.entity.*;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
    public ReservationDTO toDTO(Reservation entity) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(entity.getId());
        dto.setBookId(entity.getBook().getId());
        dto.setMemberId(entity.getMember().getId());
        dto.setReservedDate(entity.getReservedDate());
        dto.setStatus(entity.getStatus());
        return dto;
    }
    public Reservation toEntity(ReservationDTO dto) {
        Reservation entity = new Reservation();
        entity.setId(dto.getId());
        Book bookIdReference = new Book();
        bookIdReference.setId(dto.getBookId());
        entity.setBook(bookIdReference);
        Member memberIdReference = new Member();
        memberIdReference.setId(dto.getMemberId());
        entity.setMember(memberIdReference);
        entity.setReservedDate(dto.getReservedDate());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}

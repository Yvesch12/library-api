package com.example.library.mapper;
import com.example.library.dto.MemberDTO;
import com.example.library.entity.*;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {
    public MemberDTO toDTO(Member entity) {
        MemberDTO dto = new MemberDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setMembershipDate(entity.getMembershipDate());
        dto.setActive(entity.isActive());
        return dto;
    }
    public Member toEntity(MemberDTO dto) {
        Member entity = new Member();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setMembershipDate(dto.getMembershipDate());
        entity.setActive(dto.isActive());
        return entity;
    }
}

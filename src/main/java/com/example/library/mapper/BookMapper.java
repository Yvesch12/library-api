package com.example.library.mapper;
import com.example.library.dto.BookDTO;
import com.example.library.entity.*;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public BookDTO toDTO(Book entity) {
        BookDTO dto = new BookDTO();
        dto.setId(entity.getId());
        dto.setIsbn(entity.getIsbn());
        dto.setTitle(entity.getTitle());
        dto.setAuthor(entity.getAuthor());
        dto.setGenre(entity.getGenre());
        dto.setPublishedYear(entity.getPublishedYear());
        dto.setAvailable(entity.isAvailable());
        return dto;
    }
    public Book toEntity(BookDTO dto) {
        Book entity = new Book();
        entity.setId(dto.getId());
        entity.setIsbn(dto.getIsbn());
        entity.setTitle(dto.getTitle());
        entity.setAuthor(dto.getAuthor());
        entity.setGenre(dto.getGenre());
        entity.setPublishedYear(dto.getPublishedYear());
        entity.setAvailable(dto.isAvailable());
        return entity;
    }
}

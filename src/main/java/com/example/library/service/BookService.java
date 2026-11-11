package com.example.library.service;
import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @Transactional
public class BookService {
    private final BookRepository books;
    public BookService(BookRepository books) { this.books = books; }
    @Transactional(readOnly = true)
    public List<Book> list() { return books.findAll(); }
    @Transactional(readOnly = true)
    public Book get(Long id) { return books.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found")); }
    public Book create(Book book) { book.setId(null); return books.save(book); }
    public Book update(Long id, Book replacement) {
        Book book = get(id);
        book.setIsbn(replacement.getIsbn()); book.setTitle(replacement.getTitle());
        book.setAuthor(replacement.getAuthor()); book.setGenre(replacement.getGenre());
        book.setPublishedYear(replacement.getPublishedYear()); book.setAvailable(replacement.isAvailable());
        return books.save(book);
    }
    public void delete(Long id) { books.delete(get(id)); books.flush(); }
}

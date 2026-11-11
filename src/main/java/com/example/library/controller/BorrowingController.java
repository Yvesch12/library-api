package com.example.library.controller;

import com.example.library.entity.Borrowing;
import com.example.library.service.BorrowingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class BorrowingController {
    private final BorrowingService service;
    public BorrowingController(BorrowingService service) { this.service = service; }

    public record BorrowRequest(@NotNull @Positive Long bookId, @NotNull @Positive Long memberId) { }

    @PostMapping("/borrowings")
    public ResponseEntity<Borrowing> borrow(@Valid @RequestBody BorrowRequest request) {
        Borrowing loan = service.borrowBook(request.bookId(), request.memberId());
        return ResponseEntity.created(URI.create("/members/" + request.memberId() + "/borrowings")).body(loan);
    }
    @PutMapping("/borrowings/{id}/return")
    public Borrowing returnBook(@PathVariable Long id) { return service.returnBook(id); }

    @GetMapping("/members/{id}/borrowings")
    public List<Borrowing> active(@PathVariable Long id) { return service.activeBorrowings(id); }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict(IllegalStateException exception) { return Map.of("error", exception.getMessage()); }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> missing(NoSuchElementException exception) { return Map.of("error", exception.getMessage()); }
}

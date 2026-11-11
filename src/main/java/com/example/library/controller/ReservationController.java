package com.example.library.controller;
import com.example.library.entity.Reservation;
import com.example.library.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.net.URI;
import java.util.*;

@RestController
public class ReservationController {
    private final ReservationService service;
    public ReservationController(ReservationService service) { this.service = service; }
    public record ReservationRequest(@NotNull @Positive Long bookId, @NotNull @Positive Long memberId) { }
    @PostMapping("/reservations")
    public ResponseEntity<Reservation> reserve(@Valid @RequestBody ReservationRequest request) {
        Reservation result = service.reserve(request.bookId(), request.memberId());
        return ResponseEntity.created(URI.create("/members/" + request.memberId() + "/reservations")).body(result);
    }
    @GetMapping("/members/{id}/reservations")
    public List<Reservation> list(@PathVariable Long id) { return service.list(id); }
    @PutMapping("/reservations/{id}/cancel")
    public Reservation cancel(@PathVariable Long id) { return service.cancel(id); }
    @ExceptionHandler(IllegalStateException.class) @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String,String> conflict(IllegalStateException e) { return Map.of("error", e.getMessage()); }
    @ExceptionHandler(NoSuchElementException.class) @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,String> missing(NoSuchElementException e) { return Map.of("error", e.getMessage()); }
}

package com.example.library.exception;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.*;

@org.springframework.core.annotation.Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ErrorResponse(String error, Instant timestamp, Map<String,String> fields) {
        public ErrorResponse {
            fields = Map.copyOf(fields);
        }
    }
    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message, Map<String,String> fields) {
        return ResponseEntity.status(status).body(new ErrorResponse(message, Instant.now(), fields));
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> conflict(IllegalStateException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
    }
    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> missing(RuntimeException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalid(MethodArgumentNotValidException exception) {
        Map<String,String> fields = new TreeMap<>();
        exception.getBindingResult().getFieldErrors().forEach(e -> fields.put(e.getField(), e.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST, "Validation failed", fields);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> malformed(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, "Malformed request", Map.of());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integrity(DataIntegrityViolationException exception) {
        return response(HttpStatus.CONFLICT, "Resource is referenced or violates a database constraint", Map.of());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unexpected(Exception exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", Map.of());
    }
}

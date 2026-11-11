package com.example.library.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import com.example.library.entity.ReservationStatus;

public class ReservationDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(example = "1")
    private Long id;
    @Schema(example = "1")
    private Long bookId;
    @Schema(example = "1")
    private Long memberId;
    @Schema(example = "2026-11-11")
    private LocalDate reservedDate;
    @Schema(example = "ACTIVE")
    private ReservationStatus status;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public LocalDate getReservedDate() { return reservedDate; }
    public void setReservedDate(LocalDate reservedDate) { this.reservedDate = reservedDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}

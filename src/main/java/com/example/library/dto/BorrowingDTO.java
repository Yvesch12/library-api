package com.example.library.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.math.BigDecimal;

public class BorrowingDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(example = "1")
    private Long id;
    @Schema(example = "1")
    private Long bookId;
    @Schema(example = "1")
    private Long memberId;
    @Schema(example = "2026-11-11")
    private LocalDate borrowDate;
    @Schema(example = "2026-11-11")
    private LocalDate dueDate;
    @Schema(example = "2026-11-11")
    private LocalDate returnDate;
    @Schema(example = "0.00")
    private BigDecimal fineAmount;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public BigDecimal getFineAmount() { return fineAmount; }
    public void setFineAmount(BigDecimal fineAmount) { this.fineAmount = fineAmount; }
}

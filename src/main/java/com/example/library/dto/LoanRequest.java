package com.example.library.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
public record LoanRequest(@NotNull @Positive @Schema(example = "1") Long bookId,
                          @NotNull @Positive @Schema(example = "1") Long memberId) { }

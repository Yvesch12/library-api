package com.example.library.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class BookDTO {
    @Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @NotBlank @Size(max = 255)
    @Schema(example = "Example")
    private String isbn;
    @NotBlank @Size(max = 255)
    @Schema(example = "Example")
    private String title;
    @NotBlank @Size(max = 255)
    @Schema(example = "Example")
    private String author;
    @NotBlank @Size(max = 255)
    @Schema(example = "Example")
    private String genre;
    @NotNull @Min(1)
    @Schema(example = "2020")
    private Integer publishedYear;
    @Schema(example = "true")
    private boolean available = true;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Integer getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

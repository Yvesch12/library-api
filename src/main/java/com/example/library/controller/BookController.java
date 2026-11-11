package com.example.library.controller;
import com.example.library.dto.*;
import com.example.library.mapper.*;
import com.example.library.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/books")
@Tag(name = "Books", description = "Book management")
public class BookController {
    private final BookService service;
    private final BookMapper mapper;
    public BookController(BookService service, BookMapper mapper) { this.service = service; this.mapper = mapper; }
    @GetMapping @Operation(summary = "List books")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public List<BookDTO> list() { return service.list().stream().map(mapper::toDTO).toList(); }
    @GetMapping("/{id}") @Operation(summary = "Get book by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public BookDTO get(@PathVariable Long id) { return mapper.toDTO(service.get(id)); }
    @PostMapping @Operation(summary = "Create book")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "409", description = "Conflict"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public ResponseEntity<BookDTO> create(@Valid @RequestBody BookDTO dto) {
        BookDTO result = mapper.toDTO(service.create(mapper.toEntity(dto)));
        return ResponseEntity.created(URI.create("/books/" + result.getId())).body(result);
    }
    @PutMapping("/{id}") @Operation(summary = "Replace book fields")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found"), @ApiResponse(responseCode = "409", description = "Conflict"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public BookDTO update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        return mapper.toDTO(service.update(id, mapper.toEntity(dto)));
    }
    @DeleteMapping("/{id}") @Operation(summary = "Delete book")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Deleted"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found"), @ApiResponse(responseCode = "409", description = "Conflict"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}

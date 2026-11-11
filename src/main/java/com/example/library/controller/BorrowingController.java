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

@RestController @Tag(name = "Borrowings", description = "Borrowing lifecycle")
public class BorrowingController {
    private final BorrowingService service;
    private final BorrowingMapper mapper;
    public BorrowingController(BorrowingService service, BorrowingMapper mapper) { this.service = service; this.mapper = mapper; }
    @PostMapping("/borrowings") @Operation(summary = "Create borrowing")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found"), @ApiResponse(responseCode = "409", description = "Conflict"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public ResponseEntity<BorrowingDTO> create(@Valid @RequestBody LoanRequest request) {
        BorrowingDTO result = mapper.toDTO(service.borrowBook(request.bookId(), request.memberId()));
        return ResponseEntity.created(URI.create("/members/" + request.memberId() + "/borrowings")).body(result);
    }
    @PutMapping("/borrowings/{id}/return") @Operation(summary = "return borrowing")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found"), @ApiResponse(responseCode = "409", description = "Conflict"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public BorrowingDTO returnBook(@PathVariable Long id) { return mapper.toDTO(service.returnBook(id)); }
    @GetMapping("/members/{id}/borrowings") @Operation(summary = "List member borrowings")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public List<BorrowingDTO> list(@PathVariable Long id) { return service.activeBorrowings(id).stream().map(mapper::toDTO).toList(); }
}

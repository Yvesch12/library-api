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

@RestController @Tag(name = "Reservations", description = "Reservation lifecycle")
public class ReservationController {
    private final ReservationService service;
    private final ReservationMapper mapper;
    public ReservationController(ReservationService service, ReservationMapper mapper) { this.service = service; this.mapper = mapper; }
    @PostMapping("/reservations") @Operation(summary = "Create reservation")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found"), @ApiResponse(responseCode = "409", description = "Conflict"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public ResponseEntity<ReservationDTO> create(@Valid @RequestBody LoanRequest request) {
        ReservationDTO result = mapper.toDTO(service.reserve(request.bookId(), request.memberId()));
        return ResponseEntity.created(URI.create("/members/" + request.memberId() + "/reservations")).body(result);
    }
    @PutMapping("/reservations/{id}/cancel") @Operation(summary = "cancel reservation")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found"), @ApiResponse(responseCode = "409", description = "Conflict"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public ReservationDTO cancel(@PathVariable Long id) { return mapper.toDTO(service.cancel(id)); }
    @GetMapping("/members/{id}/reservations") @Operation(summary = "List member reservations")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public List<ReservationDTO> list(@PathVariable Long id) { return service.list(id).stream().map(mapper::toDTO).toList(); }
}

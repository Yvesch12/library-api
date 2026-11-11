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

@RestController @RequestMapping("/members")
@Tag(name = "Members", description = "Member management")
public class MemberController {
    private final MemberService service;
    private final MemberMapper mapper;
    public MemberController(MemberService service, MemberMapper mapper) { this.service = service; this.mapper = mapper; }
    @GetMapping @Operation(summary = "List members")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public List<MemberDTO> list() { return service.list().stream().map(mapper::toDTO).toList(); }
    @GetMapping("/{id}") @Operation(summary = "Get member by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public MemberDTO get(@PathVariable Long id) { return mapper.toDTO(service.get(id)); }
    @PostMapping @Operation(summary = "Create member")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created"), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "409", description = "Conflict"), @ApiResponse(responseCode = "500", description = "Internal error")})
    public ResponseEntity<MemberDTO> create(@Valid @RequestBody MemberDTO dto) {
        MemberDTO result = mapper.toDTO(service.create(mapper.toEntity(dto)));
        return ResponseEntity.created(URI.create("/members/" + result.getId())).body(result);
    }
}

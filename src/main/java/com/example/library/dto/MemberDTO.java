package com.example.library.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class MemberDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(example = "1")
    private Long id;
    @NotBlank @Size(max = 255)
    @Schema(example = "Example")
    private String name;
    @NotBlank @Size(max = 255)
    @Email
    @Schema(example = "alice@usj.edu.lb")
    private String email;
    @NotNull @PastOrPresent
    @Schema(example = "2026-11-11")
    private LocalDate membershipDate = LocalDate.now();
    @Schema(example = "true")
    private boolean active = true;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getMembershipDate() { return membershipDate; }
    public void setMembershipDate(LocalDate membershipDate) { this.membershipDate = membershipDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

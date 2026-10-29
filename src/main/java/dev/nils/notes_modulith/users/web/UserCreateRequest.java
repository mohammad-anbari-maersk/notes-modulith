package dev.nils.notes_modulith.users.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank @Email @Size(max = 250) String email,
        @Size(max = 500) String fullName) {}

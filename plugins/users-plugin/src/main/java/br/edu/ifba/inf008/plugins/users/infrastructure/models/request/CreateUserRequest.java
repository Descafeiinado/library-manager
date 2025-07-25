package br.edu.ifba.inf008.plugins.users.infrastructure.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @Email(message = "The email must be valid.")
        @NotBlank(message = "The email is required.")
        @Size(max = 100, message = "The email must be at most {max} characters long.")
        String email,

        @NotBlank(message = "The name is required.")
        @Size(max = 100, message = "The name must be at most {max} characters long.")
        String name) {

}

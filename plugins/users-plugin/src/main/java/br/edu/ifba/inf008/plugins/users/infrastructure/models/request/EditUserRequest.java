package br.edu.ifba.inf008.plugins.users.infrastructure.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request model for editing a existing user.
 * Contains validation annotations to ensure the email and name fields are valid.
 */
public record EditUserRequest(
        @Email(message = "The email must be valid.")
        @NotBlank(message = "The email is required.")
        @Size(max = 100, message = "The email must be at most {max} characters long.")
        String email,

        @NotBlank(message = "The name is required.")
        @Size(max = 100, message = "The name must be at most {max} characters long.")
        String name) {

}

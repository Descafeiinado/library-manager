package br.edu.ifba.inf008.plugins.loans.infrastructure.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request model for creating a new loan. Contains validation annotations to ensure the fields are
 * valid.
 */
public record CreateLoanRequest(
        @NotNull(message = "The user ID is required.")
        @Min(value = 1, message = "The user ID must be a positive number.")
        Long userId,

        @NotNull(message = "The book ID is required.")
        @Min(value = 1, message = "The book ID must be a positive number.")
        Long bookId
        ) {

}

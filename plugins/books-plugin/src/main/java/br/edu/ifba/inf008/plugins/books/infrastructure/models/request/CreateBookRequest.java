package br.edu.ifba.inf008.plugins.books.infrastructure.models.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request model for creating a new book. Contains validation annotations to ensure the fields are
 * valid.
 */
public record CreateBookRequest(
        @NotBlank(message = "The ISBN is required.")
        @Size(max = 20, message = "The ISBN must be at most {max} characters long.")
        String isbn,

        @NotBlank(message = "The title is required.")
        @Size(max = 100, message = "The title must be at most {max} characters long.")
        String title,

        @NotBlank(message = "The author is required.")
        @Size(max = 100, message = "The author must be at most {max} characters long.")
        String author,

        @NotNull(message = "The published year is required.")
        @Min(value = 1900, message = "The published year must not be earlier than 1900.")
        @Max(value = 2100, message = "The published year must not be later than 2100.")
        Integer publishedYear,

        @NotNull(message = "The number of copies available is required.")
        @Min(value = 0, message = "Copies available cannot be negative.")
        @Max(value = 1000, message = "Copies available cannot exceed {max}.")
        Integer copiesAvailable
) {

}

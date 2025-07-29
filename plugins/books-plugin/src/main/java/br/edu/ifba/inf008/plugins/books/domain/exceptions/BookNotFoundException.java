package br.edu.ifba.inf008.plugins.books.domain.exceptions;

/**
 * Exception thrown when a book with the specified ID is not found in the system.
 */
public class BookNotFoundException extends Exception {

    public BookNotFoundException(Long bookId) {
        super("Book with ID " + bookId + " not found.");
    }

}

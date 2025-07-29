package br.edu.ifba.inf008.plugins.books.domain.exceptions;

/**
 * Exception thrown when trying to register a book with an ISBN that already exists in the system.
 */
public class IsbnAlreadyExistingException extends Exception {

    public IsbnAlreadyExistingException(String isbn) {
        super("There is already a book registered with the ISBN: " + isbn);
    }

}

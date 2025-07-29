package br.edu.ifba.inf008.plugins.books.domain.exceptions;

/**
 * Exception thrown when a book has locked copies.
 */
public class BookWithLockedCopiesException extends Exception {

    public BookWithLockedCopiesException(Long bookId, Integer currentCopies) {
        super("The book with ID " + bookId + " has locked copies: " + currentCopies + ". " +
              "Please unlock the copies before proceeding.");
    }

}

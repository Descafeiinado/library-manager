package br.edu.ifba.inf008.plugins.loans.domain.exceptions;

import br.edu.ifba.inf008.plugins.books.domain.entities.Book;

/**
 * Exception thrown when a book is not available for loan.
 * This can occur if the book has no available copies or is already loaned out.
 */
public class BookNotAvailableToLoanException extends RuntimeException {

    public BookNotAvailableToLoanException(Book book) {
        super("The book '" + book.getTitle() + "' is not available for loan. " +
                "Please check the availability and try again.");
    }

}
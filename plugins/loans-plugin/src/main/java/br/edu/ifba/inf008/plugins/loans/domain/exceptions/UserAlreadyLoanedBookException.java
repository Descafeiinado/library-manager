package br.edu.ifba.inf008.plugins.loans.domain.exceptions;

import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;

/**
 * Exception thrown when a user attempts to loan a book that they have already loaned. This ensures
 * that users cannot have multiple active loans for the same book.
 */
public class UserAlreadyLoanedBookException extends RuntimeException {

    public UserAlreadyLoanedBookException(User user, Book book) {
        super("User '" + user.getName() + "' has already loaned the book '" + book.getTitle()
                + "'. " +
                "Please return the book before loaning it again.");
    }

}
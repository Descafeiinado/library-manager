package br.edu.ifba.inf008.plugins.loans.domain.exceptions;

import java.time.LocalDate;

/**
 * Exception thrown when a loan has already been returned.
 */
public class LoanAlreadyReturnedException extends RuntimeException {

    public LoanAlreadyReturnedException(Long loanId, LocalDate returnDate) {
        super("Loan with ID " + loanId + " has already been returned on " + returnDate + ".");
    }

}
package br.edu.ifba.inf008.plugins.loans.domain.exceptions;

/**
 * Exception thrown when a loan with the specified ID is not found in the system.
 */
public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(Long loanId) {
        super("Loan with ID " + loanId + " not found.");
    }

}
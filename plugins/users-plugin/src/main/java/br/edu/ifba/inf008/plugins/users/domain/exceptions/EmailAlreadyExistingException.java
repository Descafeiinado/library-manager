package br.edu.ifba.inf008.plugins.users.domain.exceptions;

/**
 * Exception thrown when trying to register a user with an email that already exists in the system.
 */
public class EmailAlreadyExistingException extends Exception {

    public EmailAlreadyExistingException(String email) {
        super("There is already a user registered with the email: " + email);
    }

}

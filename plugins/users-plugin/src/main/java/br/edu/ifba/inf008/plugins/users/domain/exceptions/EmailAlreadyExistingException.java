package br.edu.ifba.inf008.plugins.users.domain.exceptions;

public class EmailAlreadyExistingException extends Exception {

    public EmailAlreadyExistingException(String email) {
        super("There is already a user registered with the email: " + email);
    }

}

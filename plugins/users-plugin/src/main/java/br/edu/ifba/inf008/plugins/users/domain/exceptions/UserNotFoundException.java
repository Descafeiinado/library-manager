package br.edu.ifba.inf008.plugins.users.domain.exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(Long userId) {
        super("User with ID " + userId + " not found.");
    }

}

package br.edu.ifba.inf008.plugins.users.domain.exceptions;

/**
 * Exception thrown when a user with the specified ID is not found in the system.
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException(Long userId) {
        super("User with ID " + userId + " not found.");
    }

}

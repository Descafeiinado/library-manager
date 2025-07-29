package br.edu.ifba.inf008.plugins.users.application.services;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.components.BeanValidatorComponent;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.domain.exceptions.EmailAlreadyExistingException;
import br.edu.ifba.inf008.plugins.users.domain.exceptions.UserNotFoundException;
import br.edu.ifba.inf008.plugins.users.infrastructure.models.request.CreateUserRequest;
import br.edu.ifba.inf008.plugins.users.infrastructure.models.request.EditUserRequest;
import br.edu.ifba.inf008.plugins.users.infrastructure.repositories.UserRepository;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;

/**
 * Service class for managing users. Provides methods to create, edit, delete, and find users.
 */
public class UserService {

    private static final UserService instance = new UserService(UserRepository.getInstance());

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Gets the singleton instance of UserService.
     *
     * @return the UserService instance
     */
    public static UserService getInstance() {
        return instance;
    }

    /**
     * Creates a new user.
     *
     * @param request the request containing user data (name and email)
     * @return the created User entity
     * @throws ConstraintViolationException  if name or email validation fails
     * @throws EmailAlreadyExistingException if the email is already in use by another user
     */
    public User create(CreateUserRequest request)
            throws ConstraintViolationException, EmailAlreadyExistingException {
        BeanValidatorComponent.validateAndThrow(request);

        String name = request.name();
        String email = request.email();

        ensureEmailIsUnique(email, null);

        var user = new User();

        user.setName(name);
        user.setEmail(email);

        return userRepository.save(user);
    }

    /**
     * Deletes a user by setting the deactivatedAt field to the current time. This is a logical/soft
     * delete.
     *
     * @param userId the ID of the user to be deleted
     * @throws UserNotFoundException if the user does not exist or is already deactivated
     */
    public void delete(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getDeactivatedAt() != null) {
            throw new UserNotFoundException(userId);
        }

        user.setDeactivatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    /**
     * Edits an existing user.
     *
     * @param userId  the ID of the user to be edited
     * @param request the request containing updated user data (name and email)
     * @throws ConstraintViolationException  if name or email validation fails
     * @throws UserNotFoundException         if the user does not exist
     * @throws EmailAlreadyExistingException if the email is already in use by another user
     */
    public void edit(Long userId, EditUserRequest request)
            throws ConstraintViolationException, UserNotFoundException, EmailAlreadyExistingException {
        BeanValidatorComponent.validateAndThrow(request);

        String name = request.name();
        String email = request.email();

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        ensureEmailIsUnique(email, existingUser.getUserId());

        existingUser.setName(name);
        existingUser.setEmail(email);

        userRepository.save(existingUser);
    }

    /**
     * Finds all users that are not deactivated (logical/soft delete).
     *
     * @param page a one-based page index
     * @param size the size of the page to be returned
     * @return a pageable response containing users
     */
    public PageableResponse<User> findAll(int page, int size) {
        return userRepository.findAllNonDeactivated(PageRequest.of(page, size));
    }

    /**
     * Ensures that the given email is unique across all users, excluding a specific user ID.
     *
     * @param email          the email to check for uniqueness
     * @param excludedUserId the user ID to exclude from the uniqueness check
     * @throws EmailAlreadyExistingException if the email is already in use by another user
     */
    private void ensureEmailIsUnique(String email, Long excludedUserId)
            throws EmailAlreadyExistingException {
        User existing = userRepository.findByEmail(email).orElse(null);

        if (existing != null && !existing.getUserId().equals(excludedUserId)) {
            throw new EmailAlreadyExistingException(email);
        }
    }

}

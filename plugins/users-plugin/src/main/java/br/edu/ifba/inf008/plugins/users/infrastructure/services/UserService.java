package br.edu.ifba.inf008.plugins.users.infrastructure.services;

import br.edu.ifba.inf008.core.infrastructure.components.BeanValidatorComponent;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.domain.exceptions.EmailAlreadyExistingException;
import br.edu.ifba.inf008.plugins.users.domain.exceptions.UserNotFoundException;
import br.edu.ifba.inf008.plugins.users.infrastructure.models.request.CreateUserRequest;
import br.edu.ifba.inf008.plugins.users.infrastructure.models.request.EditUserRequest;
import br.edu.ifba.inf008.plugins.users.infrastructure.repositories.UserRepository;
import jakarta.validation.ConstraintViolationException;
import java.util.Optional;

public class UserService {

    private static final UserService INSTANCE = new UserService();

    private static final UserRepository userRepository = UserRepository.getInstance();

    private UserService() {
    }

    public User create(CreateUserRequest request)
            throws ConstraintViolationException, EmailAlreadyExistingException {
        BeanValidatorComponent.validateAndThrow(request);

        String name = request.name();
        String email = request.email();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistingException(email);
        }

        var user = new User();

        user.setName(name);
        user.setEmail(email);

        return userRepository.save(user);
    }

    public void edit(Long userId, EditUserRequest request)
            throws ConstraintViolationException, UserNotFoundException, EmailAlreadyExistingException {
        BeanValidatorComponent.validateAndThrow(request);

        String name = request.name();
        String email = request.email();

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        User emailConflict = userRepository.findByEmail(email)
                .orElse(null);

        if (emailConflict != null) {
            if (!emailConflict.getUserId().equals(existingUser.getUserId())) {
                throw new EmailAlreadyExistingException(email);
            }
        }

        existingUser.setName(name);
        existingUser.setEmail(email);

        userRepository.save(existingUser);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public static UserService getInstance() {
        return INSTANCE;
    }

}

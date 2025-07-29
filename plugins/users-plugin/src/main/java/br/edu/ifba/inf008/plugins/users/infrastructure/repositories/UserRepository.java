package br.edu.ifba.inf008.plugins.users.infrastructure.repositories;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.repositories.impl.HibernateRepository;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import java.util.Optional;

/**
 * Repository class for managing User entities.
 */
public class UserRepository extends HibernateRepository<User, Long> {

    /**
     * Singleton instance of UserRepository.
     */
    private static final UserRepository INSTANCE = new UserRepository();

    protected UserRepository() {
        super(User.class);
    }

    public static UserRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email the email to check
     * @return true if a user with the given email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    /**
     * Finds all users that are not deactivated.
     *
     * @param pageRequest the pagination request containing page number and size
     * @return a pageable response containing users that are not deactivated
     */
    public PageableResponse<User> findAllNonDeactivated(PageRequest pageRequest) {
        return findAll(pageRequest, "deactivatedAt", null);
    }

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user to find
     * @return an Optional containing the User if found, or empty if not found
     */
    public Optional<User> findByEmail(String email) {
        return findOne("email", email);
    }

}

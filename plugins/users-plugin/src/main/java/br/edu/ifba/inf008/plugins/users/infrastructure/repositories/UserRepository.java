package br.edu.ifba.inf008.plugins.users.infrastructure.repositories;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.repositories.impl.HibernateRepository;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import java.util.Optional;

public class UserRepository extends HibernateRepository<User, Long> {

    private static final UserRepository INSTANCE = new UserRepository();

    private UserRepository() {
        super(User.class);
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public PageableResponse<User> findAllNonDeactivated(PageRequest pageRequest) {
        return findAll(pageRequest, "deactivatedAt", null);
    }

    public Optional<User> findByEmail(String email) {
        return findOne("email", email);
    }

    public static UserRepository getInstance() {
        return INSTANCE;
    }

}

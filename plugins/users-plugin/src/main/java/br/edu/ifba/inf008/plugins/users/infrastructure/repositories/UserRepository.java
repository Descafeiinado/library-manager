package br.edu.ifba.inf008.plugins.users.infrastructure.repositories;

import br.edu.ifba.inf008.core.infrastructure.repositories.impl.HibernateRepository;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;

public class UserRepository extends HibernateRepository<User, Long> {

    private static final UserRepository INSTANCE = new UserRepository();

    public UserRepository() {
        super(User.class);
    }

    public static UserRepository getInstance() {
        return INSTANCE;
    }

}

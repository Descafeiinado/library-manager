package br.edu.ifba.inf008.plugins.users.infrastructure.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    private SessionFactory sessionFactory;
    private UserRepository userRepository;

    @BeforeAll
    void setup() {
        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

        userRepository = new UserRepository() {
            @Override
            public Session getSession() {
                return sessionFactory.openSession();
            }
        };
    }

    @BeforeEach
    void init() {
        try (Session session = userRepository.getSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterAll
    void tearDown() {
        sessionFactory.close();
    }

    @Test
    void saveAndFindByEmail_ShouldWork() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("alice@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
    }

    @Test
    void existsByEmail_ShouldReturnTrueIfExists() {
        User user = new User();
        user.setName("Bob");
        user.setEmail("bob@example.com");
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("bob@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("unknown@example.com")).isFalse();
    }

    @Test
    void findAllNonDeactivated_ShouldReturnOnlyActiveUsers() {
        User activeUser = new User();
        activeUser.setName("Active");
        activeUser.setEmail("active@example.com");
        activeUser.setDeactivatedAt(null);

        User deactivatedUser = new User();
        deactivatedUser.setName("Deactivated");
        deactivatedUser.setEmail("deactivated@example.com");
        deactivatedUser.setDeactivatedAt(java.time.LocalDateTime.now());

        userRepository.save(activeUser);
        userRepository.save(deactivatedUser);

        var pageRequest = PageRequest.of(0, 10);
        PageableResponse<User> page = userRepository.findAllNonDeactivated(pageRequest);

        assertThat(page.getContent())
                .extracting(User::getEmail)
                .containsExactly("active@example.com");
    }

}

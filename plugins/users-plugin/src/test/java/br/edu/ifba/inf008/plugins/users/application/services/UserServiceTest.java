package br.edu.ifba.inf008.plugins.users.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.edu.ifba.inf008.plugins.users.application.services.UserService;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.domain.exceptions.EmailAlreadyExistingException;
import br.edu.ifba.inf008.plugins.users.domain.exceptions.UserNotFoundException;
import br.edu.ifba.inf008.plugins.users.infrastructure.models.request.CreateUserRequest;
import br.edu.ifba.inf008.plugins.users.infrastructure.models.request.EditUserRequest;
import br.edu.ifba.inf008.plugins.users.infrastructure.repositories.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    private SessionFactory sessionFactory;
    private UserRepository userRepository;
    private UserService userService;

    @BeforeAll
    public void setup() {
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

        userRepository = new UserRepository() {
            @Override
            public Session getSession() {
                return sessionFactory.openSession();
            }
        };

        userService = new UserService(userRepository);
    }

    @AfterAll
    public void tearDown() {
        sessionFactory.close();
    }

    @Test
    void createUser_Success() throws EmailAlreadyExistingException {
        CreateUserRequest request = new CreateUserRequest("real@example.com", "Real User");

        User created = userService.create(request);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Real User");
        assertThat(created.getEmail()).isEqualTo("real@example.com");
    }

    @Test
    void createUser_ThrowsWhenEmailExists() throws EmailAlreadyExistingException {
        CreateUserRequest request1 = new CreateUserRequest("dup@example.com", "User1");

        userService.create(request1);

        CreateUserRequest request2 = new CreateUserRequest("dup@example.com", "User2");

        assertThatThrownBy(() -> userService.create(request2))
                .isInstanceOf(EmailAlreadyExistingException.class);
    }

    @Test
    void deleteUser_Success() throws EmailAlreadyExistingException, UserNotFoundException {
        CreateUserRequest request = new CreateUserRequest("delete@example.com", "ToDelete");
        User user = userService.create(request);

        userService.delete(user.getUserId());

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User updated = session.get(User.class, user.getUserId());
            tx.commit();

            assertThat(updated.getDeactivatedAt()).isNotNull();
        }
    }

    @Test
    void editUser_Success() throws EmailAlreadyExistingException, UserNotFoundException {
        CreateUserRequest createRequest = new CreateUserRequest("old@example.com", "Old Name");
        User user = userService.create(createRequest);

        EditUserRequest editRequest = new EditUserRequest("new@example.com", "New Name");
        userService.edit(user.getUserId(), editRequest);

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User updated = session.get(User.class, user.getUserId());
            tx.commit();

            assertThat(updated.getName()).isEqualTo("New Name");
            assertThat(updated.getEmail()).isEqualTo("new@example.com");
        }
    }

    @Test
    void editUser_ThrowsWhenEmailConflict() throws EmailAlreadyExistingException {
        CreateUserRequest user1 = new CreateUserRequest("one@example.com", "User One");
        CreateUserRequest user2 = new CreateUserRequest("two@example.com", "User Two");

        User u1 = userService.create(user1);
        User u2 = userService.create(user2);

        EditUserRequest conflictRequest = new EditUserRequest("one@example.com", "Conflict");

        assertThatThrownBy(() -> userService.edit(u2.getUserId(), conflictRequest))
                .isInstanceOf(EmailAlreadyExistingException.class);
    }
}

package br.edu.ifba.inf008.plugins.loans.infrastructure.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.loans.domain.entities.Loan;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import java.time.LocalDate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanRepositoryTest {

    private SessionFactory sessionFactory;
    private LoanRepository loanRepository;

    @BeforeAll
    void setup() {
        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(Loan.class)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Book.class)
                .buildSessionFactory();

        loanRepository = new LoanRepository() {
            @Override
            public Session getSession() {
                return sessionFactory.openSession();
            }
        };
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = loanRepository.getSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM Loan").executeUpdate();
            session.createQuery("DELETE FROM Book").executeUpdate();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterAll
    void tearDown() {
        sessionFactory.close();
    }

    @Test
    void countByBookIdAndNotReturned_ShouldReturnCorrectCount() {
        try (Session session = loanRepository.getSession()) {
            session.beginTransaction();

            Book book = new Book();
            book.setTitle("Test Book");
            book.setAuthor("Author");
            book.setIsbn("1234567890");
            book.setPublishedYear(2020);
            book.setCopiesAvailable(3);
            session.persist(book);

            User user = new User();
            user.setName("John");
            user.setEmail("john@nobody.br");
            session.persist(user);

            Loan loan1 = new Loan();
            loan1.setBook(book);
            loan1.setUser(user);
            session.persist(loan1);

            Loan loan2 = new Loan();
            loan2.setBook(book);
            loan2.setUser(user);
            loan2.setReturnDate(LocalDate.now());
            session.persist(loan2);

            session.getTransaction().commit();

            Long count = loanRepository.countByBookIdAndNotReturned(book.getBookId());
            assertThat(count).isEqualTo(1L);
        }
    }

    @Test
    void existsByUserIdAndBookIdAndNotReturned_ShouldReturnTrueWhenExists() {
        try (Session session = loanRepository.getSession()) {
            session.beginTransaction();

            Book book = new Book();
            book.setTitle("Another Book");
            book.setAuthor("Author");
            book.setIsbn("2222222222");
            book.setPublishedYear(2019);
            book.setCopiesAvailable(5);
            session.persist(book);

            User user = new User();
            user.setName("Jane");
            user.setEmail("jane@doe.net");
            session.persist(user);

            Loan loan = new Loan();
            loan.setBook(book);
            loan.setUser(user);

            session.persist(loan);

            session.getTransaction().commit();

            boolean exists = loanRepository.existsByUserIdAndBookIdAndNotReturned(user.getUserId(),
                    book.getBookId());
            assertThat(exists).isTrue();
        }
    }

    @Test
    void existsByUserIdAndBookIdAndNotReturned_ShouldReturnFalseWhenNoActiveLoan() {
        try (Session session = loanRepository.getSession()) {
            session.beginTransaction();

            Book book = new Book();
            book.setTitle("Some Book");
            book.setAuthor("Author");
            book.setIsbn("1111111111");
            book.setPublishedYear(2010);
            book.setCopiesAvailable(1);
            session.persist(book);

            User user = new User();
            user.setName("Michael");
            user.setEmail("michael@jackson.com");
            session.persist(user);

            Loan loan = new Loan();
            loan.setBook(book);
            loan.setUser(user);
            loan.setReturnDate(LocalDate.now());
            session.persist(loan);

            session.getTransaction().commit();

            boolean exists = loanRepository.existsByUserIdAndBookIdAndNotReturned(user.getUserId(),
                    book.getBookId());
            assertThat(exists).isFalse();
        }
    }
}

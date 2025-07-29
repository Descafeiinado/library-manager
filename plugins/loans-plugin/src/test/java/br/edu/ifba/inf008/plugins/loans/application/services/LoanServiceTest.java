package br.edu.ifba.inf008.plugins.loans.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.domain.exceptions.BookNotFoundException;
import br.edu.ifba.inf008.plugins.books.infrastructure.managers.BookAvailabilityManager;
import br.edu.ifba.inf008.plugins.books.infrastructure.repositories.BookRepository;
import br.edu.ifba.inf008.plugins.loans.domain.entities.Loan;
import br.edu.ifba.inf008.plugins.loans.domain.exceptions.BookNotAvailableToLoanException;
import br.edu.ifba.inf008.plugins.loans.domain.exceptions.LoanAlreadyReturnedException;
import br.edu.ifba.inf008.plugins.loans.domain.exceptions.LoanNotFoundException;
import br.edu.ifba.inf008.plugins.loans.domain.exceptions.UserAlreadyLoanedBookException;
import br.edu.ifba.inf008.plugins.loans.infrastructure.models.request.CreateLoanRequest;
import br.edu.ifba.inf008.plugins.loans.infrastructure.repositories.LoanRepository;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.domain.exceptions.UserNotFoundException;
import br.edu.ifba.inf008.plugins.users.infrastructure.repositories.UserRepository;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoanServiceTest {

    private SessionFactory sessionFactory;
    private LoanRepository loanRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private BookAvailabilityManager bookAvailabilityManager;
    private LoanService loanService;

    private User createUser(String name) {
        User user = new User();
        user.setName(name);
        user.setEmail(name.toLowerCase().replace(" ", ".") + "@test.com");
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
        return user;
    }

    private Book createBook(String title, int copies) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor("Author");
        book.setIsbn(String.valueOf(System.nanoTime()));
        book.setPublishedYear(2023);
        book.setCopiesAvailable(copies);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(book);
            session.getTransaction().commit();
        }
        return book;
    }

    @BeforeAll
    public void setup() {
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(Loan.class)
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

        loanRepository = new LoanRepository() {
            @Override
            public Session getSession() {
                return sessionFactory.openSession();
            }
        };
        bookRepository = new BookRepository() {
            @Override
            public Session getSession() {
                return sessionFactory.openSession();
            }
        };
        userRepository = new UserRepository() {
            @Override
            public Session getSession() {
                return sessionFactory.openSession();
            }
        };

        bookAvailabilityManager = new BookAvailabilityManager(bookRepository);
        loanService = new LoanService(loanRepository, bookRepository, userRepository,
                bookAvailabilityManager);
    }

    @AfterAll
    public void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("delete from Loan").executeUpdate();
            session.createMutationQuery("delete from Book").executeUpdate();
            session.createMutationQuery("delete from User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Nested
    class CreateLoanTests {

        @Test
        void createLoan_Success() throws Exception {
            User user = createUser("John Doe");
            Book book = createBook("The Great Gatsby", 1);
            CreateLoanRequest request = new CreateLoanRequest(user.getUserId(), book.getBookId());

            Loan createdLoan = loanService.create(request);

            assertThat(createdLoan).isNotNull();
            assertThat(createdLoan.getLoanId()).isNotNull();
            assertThat(createdLoan.getUser().getUserId()).isEqualTo(user.getUserId());
            assertThat(createdLoan.getBook().getBookId()).isEqualTo(book.getBookId());
            assertThat(createdLoan.getLoanDate()).isEqualTo(LocalDate.now());
            assertThat(createdLoan.getReturnDate()).isNull();
        }

        @Test
        void createLoan_ThrowsWhenUserNotFound() {
            Book book = createBook("1984", 2);
            CreateLoanRequest request = new CreateLoanRequest(999L, book.getBookId());

            assertThatThrownBy(() -> loanService.create(request))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void createLoan_ThrowsWhenBookNotFound() {
            User user = createUser("Jane Smith");
            CreateLoanRequest request = new CreateLoanRequest(user.getUserId(), 999L);

            assertThatThrownBy(() -> loanService.create(request))
                    .isInstanceOf(BookNotFoundException.class);
        }

        @Test
        void createLoan_ThrowsWhenBookNotAvailable() {
            User user = createUser("Alice Johnson");
            Book book = createBook("To Kill a Mockingbird", 0);
            CreateLoanRequest request = new CreateLoanRequest(user.getUserId(), book.getBookId());

            assertThatThrownBy(() -> loanService.create(request))
                    .isInstanceOf(BookNotAvailableToLoanException.class);
        }

        @Test
        void createLoan_ThrowsWhenUserAlreadyLoanedBook() throws Exception {
            User user = createUser("Bob Brown");
            Book book = createBook("Pride and Prejudice", 2);

            loanService.create(new CreateLoanRequest(user.getUserId(), book.getBookId()));

            CreateLoanRequest duplicateRequest = new CreateLoanRequest(user.getUserId(),
                    book.getBookId());
            assertThatThrownBy(() -> loanService.create(duplicateRequest))
                    .isInstanceOf(UserAlreadyLoanedBookException.class);
        }
    }

    @Nested
    class ReturnLoanTests {

        @Test
        void markAsReturned_Success() throws Exception {
            User user = createUser("Charlie Davis");
            Book book = createBook("The Catcher in the Rye", 1);
            Loan loan = loanService.create(
                    new CreateLoanRequest(user.getUserId(), book.getBookId()));

            loanService.markAsReturned(loan.getLoanId());

            try (Session session = sessionFactory.openSession()) {
                Loan updatedLoan = session.get(Loan.class, loan.getLoanId());
                assertThat(updatedLoan.getReturnDate()).isNotNull();
                assertThat(updatedLoan.getReturnDate()).isEqualTo(LocalDate.now());
            }
        }

        @Test
        void markAsReturned_ThrowsWhenLoanNotFound() {
            assertThatThrownBy(() -> loanService.markAsReturned(999L))
                    .isInstanceOf(LoanNotFoundException.class);
        }

        @Test
        void markAsReturned_ThrowsWhenAlreadyReturned() throws Exception {
            User user = createUser("Diana Prince");
            Book book = createBook("Moby Dick", 1);
            Loan loan = loanService.create(
                    new CreateLoanRequest(user.getUserId(), book.getBookId()));

            loanService.markAsReturned(loan.getLoanId());

            assertThatThrownBy(() -> loanService.markAsReturned(loan.getLoanId()))
                    .isInstanceOf(LoanAlreadyReturnedException.class);
        }
    }

    @Nested
    class FindAllLoansTests {

        @Test
        void findAll_ReturnsPageableResponse() throws Exception {
            User user1 = createUser("User One");
            User user2 = createUser("User Two");
            Book book1 = createBook("Book One", 3);
            Book book2 = createBook("Book Two", 3);

            loanService.create(new CreateLoanRequest(user1.getUserId(), book1.getBookId()));
            loanService.create(new CreateLoanRequest(user2.getUserId(), book2.getBookId()));

            var response = loanService.findAll(0, 10);

            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(2);
            assertThat(response.getTotalElements()).isEqualTo(2);
            assertThat(response.getPage()).isEqualTo(0);
        }
    }

    @Nested
    class ValidationTests {

        @Test
        void createLoan_ThrowsWhenUserIdIsNull() {
            Book book = createBook("A Valid Book", 1);
            CreateLoanRequest request = new CreateLoanRequest(null, book.getBookId());

            assertThatThrownBy(() -> loanService.create(request))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        void createLoan_ThrowsWhenBookIdIsNull() {
            User user = createUser("A Valid User");
            CreateLoanRequest request = new CreateLoanRequest(user.getUserId(), null);

            assertThatThrownBy(() -> loanService.create(request))
                    .isInstanceOf(ConstraintViolationException.class);
        }
    }
}

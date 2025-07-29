package br.edu.ifba.inf008.plugins.books.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.domain.exceptions.BookNotFoundException;
import br.edu.ifba.inf008.plugins.books.domain.exceptions.IsbnAlreadyExistingException;
import br.edu.ifba.inf008.plugins.books.infrastructure.models.request.CreateBookRequest;
import br.edu.ifba.inf008.plugins.books.infrastructure.models.request.EditBookRequest;
import br.edu.ifba.inf008.plugins.books.infrastructure.repositories.BookRepository;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookServiceTest {

    private SessionFactory sessionFactory;
    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeAll
    public void setup() {
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(Book.class)
                .buildSessionFactory();

        bookRepository = new BookRepository() {
            @Override
            public Session getSession() {
                return sessionFactory.openSession();
            }
        };

        bookService = new BookService(bookRepository);
    }

    @AfterAll
    public void tearDown() {
        sessionFactory.close();
    }

    @Test
    void createBook_Success() throws Exception {
        CreateBookRequest request = new CreateBookRequest(
                "1234567890123", "Book Title", "Author Name", 2020, 5
        );

        Book created = bookService.create(request);

        assertThat(created).isNotNull();
        assertThat(created.getIsbn()).isEqualTo("1234567890123");
        assertThat(created.getTitle()).isEqualTo("Book Title");
        assertThat(created.getAuthor()).isEqualTo("Author Name");
        assertThat(created.getPublishedYear()).isEqualTo(2020);
        assertThat(created.getCopiesAvailable()).isEqualTo(5);
    }

    @Test
    void createBook_ThrowsWhenIsbnExists() throws Exception {
        String isbn = "1112223334445";

        bookService.create(new CreateBookRequest(isbn, "Book A", "Author A", 2021, 3));

        CreateBookRequest duplicate = new CreateBookRequest(isbn, "Book B", "Author B", 2022, 4);

        assertThatThrownBy(() -> bookService.create(duplicate))
                .isInstanceOf(IsbnAlreadyExistingException.class);
    }

    @Test
    void deleteBook_Success() throws Exception {
        Book book = bookService.create(new CreateBookRequest(
                "9999999999999", "To Delete", "Author", 2020, 2
        ));

        bookService.delete(book.getBookId());

        try (Session session = sessionFactory.openSession()) {
            Book updated = session.get(Book.class, book.getBookId());
            assertThat(updated.getDeactivatedAt()).isNotNull();
            assertThat(updated.getIsbn()).startsWith("9999999999999#");
        }
    }

    @Test
    void deleteBook_ThrowsWhenAlreadyDeactivated() throws Exception {
        Book book = bookService.create(new CreateBookRequest(
                "2223334445556", "To Deactivate", "Auth", 2018, 2
        ));

        bookService.delete(book.getBookId());

        assertThatThrownBy(() -> bookService.delete(book.getBookId()))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void editBook_Success() throws Exception {
        Book book = bookService.create(new CreateBookRequest(
                "3334445556667", "Original Title", "Author", 2015, 5
        ));

        EditBookRequest editRequest = new EditBookRequest(
                "3334445556667", "Edited Title", "New Author", 2016, 3
        );

        Book edited = bookService.edit(book.getBookId(), editRequest);

        assertThat(edited.getTitle()).isEqualTo("Edited Title");
        assertThat(edited.getAuthor()).isEqualTo("New Author");
        assertThat(edited.getPublishedYear()).isEqualTo(2016);
        assertThat(edited.getCopiesAvailable()).isEqualTo(3);
    }

    @Test
    void editBook_ThrowsWhenIsbnExists() throws Exception {
        Book b1 = bookService.create(new CreateBookRequest("7778889990001", "A", "A", 2011, 1));
        Book b2 = bookService.create(new CreateBookRequest("0001112223334", "B", "B", 2012, 1));

        EditBookRequest request = new EditBookRequest(
                "7778889990001", "Changed", "Changed", 2013, 2
        );

        assertThatThrownBy(() -> bookService.edit(b2.getBookId(), request))
                .isInstanceOf(IsbnAlreadyExistingException.class);
    }

    @Test
    void editBook_ThrowsWhenBookNotFound() {
        EditBookRequest request = new EditBookRequest(
                "9990001112223", "Doesn't Matter", "N/A", 2000, 1
        );

        assertThatThrownBy(() -> bookService.edit(999L, request))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void findAll_ReturnsPageableResponse() {
        var response = bookService.findAll(1, 10);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isNotNull();
    }

    @Nested
    class BookValidationTests {

        @Test
        void createBook_ThrowsWhenIsbnIsBlank() {
            CreateBookRequest request = new CreateBookRequest(
                    " ", "Valid Title", "Valid Author", 2000, 5
            );

            assertThatThrownBy(() -> bookService.create(request))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        void createBook_ThrowsWhenTitleIsTooLong() {
            String longTitle = "T".repeat(101);
            CreateBookRequest request = new CreateBookRequest(
                    "1234567890123", longTitle, "Author", 2001, 2
            );

            assertThatThrownBy(() -> bookService.create(request))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        void createBook_ThrowsWhenPublishedYearIsTooLow() {
            CreateBookRequest request = new CreateBookRequest(
                    "1234567890123", "Title", "Author", 1899, 1
            );

            assertThatThrownBy(() -> bookService.create(request))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        void createBook_ThrowsWhenCopiesAreNegative() {
            CreateBookRequest request = new CreateBookRequest(
                    "1234567890123", "Title", "Author", 2000, -1
            );

            assertThatThrownBy(() -> bookService.create(request))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        void editBook_ThrowsWhenAuthorIsBlank() throws Exception {
            Book book = bookService.create(new CreateBookRequest(
                    "5554443332221", "Title", "Author", 2005, 2
            ));

            EditBookRequest invalidRequest = new EditBookRequest(
                    "5554443332221", "Title", " ", 2005, 2
            );

            assertThatThrownBy(() -> bookService.edit(book.getBookId(), invalidRequest))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        void editBook_ThrowsWhenPublishedYearIsTooHigh() throws Exception {
            Book book = bookService.create(new CreateBookRequest(
                    "6665554443332", "Title", "Author", 2005, 2
            ));

            EditBookRequest invalidRequest = new EditBookRequest(
                    "6665554443332", "Title", "Author", 2101, 2
            );

            assertThatThrownBy(() -> bookService.edit(book.getBookId(), invalidRequest))
                    .isInstanceOf(ConstraintViolationException.class);
        }
    }

}


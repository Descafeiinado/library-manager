package br.edu.ifba.inf008.plugins.books.infrastructure.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import java.time.LocalDateTime;
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
class BookRepositoryTest {

    private SessionFactory sessionFactory;
    private BookRepository bookRepository;

    @BeforeAll
    void setup() {
        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(Book.class)
                .buildSessionFactory();

        bookRepository = new BookRepository() {
            @Override
            public Session getSession() {
                return sessionFactory.openSession();
            }
        };
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = bookRepository.getSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM Book").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterAll
    void tearDown() {
        sessionFactory.close();
    }

    @Test
    void saveAndFindByIsbn_ShouldWork() {
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setIsbn("9780134685991");
        book.setPublishedYear(2018);
        book.setCopiesAvailable(10);

        bookRepository.save(book);

        Optional<Book> found = bookRepository.findByIsbn("9780134685991");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Effective Java");
    }

    @Test
    void findByIsbn_ShouldReturnEmptyIfNotFound() {
        Optional<Book> found = bookRepository.findByIsbn("non-existent");
        assertThat(found).isEmpty();
    }

    @Test
    void findAllNonDeactivated_ShouldReturnOnlyActiveBooks() {
        Book activeBook = new Book();
        activeBook.setTitle("Clean Code");
        activeBook.setAuthor("Robert C. Martin");
        activeBook.setIsbn("9780132350884");
        activeBook.setPublishedYear(2008);
        activeBook.setCopiesAvailable(5);

        Book deactivatedBook = new Book();
        deactivatedBook.setTitle("Obsolete Book");
        deactivatedBook.setAuthor("Old Author");
        deactivatedBook.setIsbn("1234567890123");
        deactivatedBook.setPublishedYear(1901);
        deactivatedBook.setCopiesAvailable(0);
        deactivatedBook.setDeactivatedAt(LocalDateTime.now());

        bookRepository.save(activeBook);
        bookRepository.save(deactivatedBook);

        PageableResponse<Book> page = bookRepository.findAllNonDeactivated(PageRequest.of(0, 10));

        assertThat(page.getContent())
                .extracting(Book::getTitle)
                .containsExactly("Clean Code")
                .doesNotContain("Obsolete Book");
    }
}

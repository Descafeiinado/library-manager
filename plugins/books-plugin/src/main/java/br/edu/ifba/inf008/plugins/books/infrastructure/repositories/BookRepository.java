package br.edu.ifba.inf008.plugins.books.infrastructure.repositories;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.repositories.impl.HibernateRepository;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing Book entities.
 */
public class BookRepository extends HibernateRepository<Book, Long> {

    /**
     * Singleton instance of BookRepository.
     */
    private static final BookRepository INSTANCE = new BookRepository();

    protected BookRepository() {
        super(Book.class);
    }

    public static BookRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Finds all books that are not deactivated.
     *
     * @param pageRequest the pagination request containing page number and size
     * @return a pageable response containing books that are not deactivated
     */
    public PageableResponse<Book> findAllNonDeactivated(PageRequest pageRequest) {
        return findAll(pageRequest, "deactivatedAt", null);
    }

    /**
     * Finds all books that are not deactivated.
     *
     * @return a response containing books that are not deactivated
     */
    public List<Book> findAllNonDeactivated() {
        return findAll("deactivatedAt", null);
    }

    /**
     * Finds a book by their isbn.
     *
     * @param isbn the isbn of the book to find
     * @return an Optional containing the Book if found, or empty if not found
     */
    public Optional<Book> findByIsbn(String isbn) {
        return findOne("isbn", isbn);
    }

}

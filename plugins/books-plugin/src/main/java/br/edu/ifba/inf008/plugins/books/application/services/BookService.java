package br.edu.ifba.inf008.plugins.books.application.services;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.components.BeanValidatorComponent;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.domain.exceptions.BookNotFoundException;
import br.edu.ifba.inf008.plugins.books.domain.exceptions.BookWithLockedCopiesException;
import br.edu.ifba.inf008.plugins.books.domain.exceptions.IsbnAlreadyExistingException;
import br.edu.ifba.inf008.plugins.books.infrastructure.managers.BookAvailabilityManager;
import br.edu.ifba.inf008.plugins.books.infrastructure.models.request.CreateBookRequest;
import br.edu.ifba.inf008.plugins.books.infrastructure.models.request.EditBookRequest;
import br.edu.ifba.inf008.plugins.books.infrastructure.repositories.BookRepository;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;

/**
 * Service class for managing books. Provides methods to create, edit, delete, and find books.
 */
public class BookService {

    private static final BookService instance = new BookService(BookRepository.getInstance());

    private final BookAvailabilityManager bookAvailabilityManager;
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.bookAvailabilityManager = new BookAvailabilityManager(bookRepository);
    }

    /**
     * Gets the singleton instance of BookService.
     *
     * @return the BookService instance
     */
    public static BookService getInstance() {
        return instance;
    }

    /**
     * Creates a new book.
     *
     * @param request the request containing book data
     * @return the created Book entity
     * @throws ConstraintViolationException if any validation fails
     * @throws IsbnAlreadyExistingException if the isbn is already in use by another book
     */
    public Book create(CreateBookRequest request)
            throws ConstraintViolationException, IsbnAlreadyExistingException {
        BeanValidatorComponent.validateAndThrow(request);

        String title = request.title();
        String author = request.author();
        String isbn = request.isbn();
        Integer publishedYear = request.publishedYear();
        Integer copiesAvailable = request.copiesAvailable();

        ensureIsbnIsUnique(isbn, null);

        var book = new Book();

        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublishedYear(publishedYear);
        book.setCopiesAvailable(copiesAvailable);

        return bookRepository.save(book);
    }

    /**
     * Deletes a book by setting the deactivatedAt field to the current time. This is a logical/soft
     * delete.
     *
     * @param bookId the ID of the book to be deleted
     * @throws BookNotFoundException if the book does not exist or is already deactivated
     */
    public void delete(Long bookId) throws BookNotFoundException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (book.getDeactivatedAt() != null) {
            throw new BookNotFoundException(bookId);
        }

        book.setIsbn(
                book.getIsbn() + "#" + book.getBookId()); // Append bookId to ISBN for uniqueness
        book.setDeactivatedAt(LocalDateTime.now());

        bookRepository.save(book);
    }

    /**
     * Edits an existing book.
     *
     * @param bookId  the ID of the book to be edited
     * @param request the request containing updated book data
     * @return the updated Book entity
     * @throws ConstraintViolationException if any validation fails
     * @throws BookNotFoundException        if the book does not exist
     * @throws IsbnAlreadyExistingException if the isbn is already in use by another book
     */
    public Book edit(Long bookId, EditBookRequest request)
            throws ConstraintViolationException, BookNotFoundException, IsbnAlreadyExistingException, BookWithLockedCopiesException {
        BeanValidatorComponent.validateAndThrow(request);

        String title = request.title();
        String author = request.author();
        String isbn = request.isbn();
        Integer publishedYear = request.publishedYear();
        Integer copiesAvailable = request.copiesAvailable();

        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        ensureIsbnIsUnique(isbn, existingBook.getBookId());
        ensureBookIsNotLocked(existingBook, copiesAvailable);

        existingBook.setTitle(title);
        existingBook.setAuthor(author);
        existingBook.setPublishedYear(publishedYear);
        existingBook.setCopiesAvailable(copiesAvailable);

        return bookRepository.save(existingBook);
    }

    /**
     * Finds all books that are not deactivated (logical/soft delete).
     *
     * @param page a one-based page index
     * @param size the size of the page to be returned
     * @return a pageable response containing books
     */
    public PageableResponse<Book> findAll(int page, int size) {
        return bookRepository.findAllNonDeactivated(PageRequest.of(page, size));
    }

    /**
     * Ensures that the given isbn is unique across all books, excluding a specific book ID.
     *
     * @param isbn           the isbn to check for uniqueness
     * @param excludedBookId the book ID to exclude from the uniqueness check
     * @throws IsbnAlreadyExistingException if the isbn is already in use by another book
     */
    private void ensureIsbnIsUnique(String isbn, Long excludedBookId)
            throws IsbnAlreadyExistingException {
        Book existing = bookRepository.findByIsbn(isbn).orElse(null);

        if (existing != null && !existing.getBookId().equals(excludedBookId)) {
            throw new IsbnAlreadyExistingException(isbn);
        }
    }

    /**
     * Ensures that the book is not locked by checking if the number of available copies is less than
     * the requested new total copies available.
     *
     * @param book                  the book to check
     * @param newTotalCopiesAvailable the new total copies available to set
     * @throws BookWithLockedCopiesException if the book has locked copies that prevent the update
     */
    private void ensureBookIsNotLocked(Book book, Integer newTotalCopiesAvailable)
            throws BookWithLockedCopiesException {
        Long availableCopies = bookAvailabilityManager.getAvailableCopies(book.getBookId());
        int currentTotal = book.getCopiesAvailable();

        Long lockedCopies = currentTotal - availableCopies;

        if (newTotalCopiesAvailable < lockedCopies) {
            throw new BookWithLockedCopiesException(book.getBookId(),
                    newTotalCopiesAvailable - lockedCopies);
        }
    }

}

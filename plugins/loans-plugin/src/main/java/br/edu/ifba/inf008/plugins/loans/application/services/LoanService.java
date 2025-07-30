package br.edu.ifba.inf008.plugins.loans.application.services;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.components.BeanValidatorComponent;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.domain.exceptions.BookNotFoundException;
import br.edu.ifba.inf008.plugins.books.infrastructure.managers.BookAvailabilityManager;
import br.edu.ifba.inf008.plugins.books.infrastructure.repositories.BookRepository;
import br.edu.ifba.inf008.plugins.loans.application.extensions.reports.models.LoanedBookInformationModel;
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
import java.util.Objects;

/**
 * Service class for managing loans. Provides methods to create, edit, delete, and find books.
 */
public class LoanService {

    private static final LoanService instance = new LoanService(LoanRepository.getInstance(),
            BookRepository.getInstance(), UserRepository.getInstance(),
            BookAvailabilityManager.getInstance());

    private final LoanRepository loanRepository;

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private final BookAvailabilityManager bookAvailabilityManager;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository,
            UserRepository userRepository, BookAvailabilityManager bookAvailabilityManager) {
        this.loanRepository = loanRepository;

        this.bookRepository = bookRepository;
        this.userRepository = userRepository;

        this.bookAvailabilityManager = bookAvailabilityManager;
    }

    /**
     * Gets the singleton instance of BookService.
     *
     * @return the BookService instance
     */
    public static LoanService getInstance() {
        return instance;
    }

    /**
     * Creates a new loan based on the provided request.
     *
     * @param request the request containing the user ID and book ID for the loan
     * @return the created Loan entity
     * @throws ConstraintViolationException    if the request validation fails
     * @throws UserNotFoundException           if the user with the given ID does not exist
     * @throws BookNotFoundException           if the book with the given ID does not exist
     * @throws BookNotAvailableToLoanException if the book is not available for loan
     * @throws UserAlreadyLoanedBookException  if the user has already loaned this book
     */
    public Loan create(CreateLoanRequest request)
            throws ConstraintViolationException, UserNotFoundException, BookNotFoundException, BookNotAvailableToLoanException, UserAlreadyLoanedBookException {
        BeanValidatorComponent.validateAndThrow(request);

        Long userId = request.userId();
        Long bookId = request.bookId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (loanRepository.existsByUserIdAndBookIdAndNotReturned(userId, bookId)) {
            throw new UserAlreadyLoanedBookException(user, book);
        }

        if (!bookAvailabilityManager.isBookAvailable(book.getBookId())) {
            throw new BookNotAvailableToLoanException(book);
        }

        Loan loan = new Loan();

        loan.setUser(user);
        loan.setBook(book);

        loan.setLoanDate(LocalDate.now());

        return loanRepository.save(loan);
    }

    /**
     * Marks a loan as returned by setting the return date to the current date.
     *
     * @param loanId the ID of the loan to be marked as returned
     * @throws LoanNotFoundException        if the loan with the given ID does not exist
     * @throws LoanAlreadyReturnedException if the loan has already been returned
     */
    public void markAsReturned(Long loanId)
            throws LoanNotFoundException, LoanAlreadyReturnedException {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        if (!Objects.isNull(loan.getReturnDate())) {
            throw new LoanAlreadyReturnedException(loanId, loan.getReturnDate());
        }

        loan.setReturnDate(LocalDate.now());

        loanRepository.save(loan);
    }

    /**
     * Finds all books that are not deactivated (logical/soft delete).
     *
     * @param page a one-based page index
     * @param size the size of the page to be returned
     * @return a pageable response containing books
     */
    public PageableResponse<Loan> findAll(int page, int size) {
        return loanRepository.findAll(PageRequest.of(page, size));
    }

}

package br.edu.ifba.inf008.plugins.loans.infrastructure.providers;

import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.infrastructure.managers.BookAvailabilityManager;
import br.edu.ifba.inf008.plugins.books.infrastructure.providers.BookAvailabilityProvider;
import br.edu.ifba.inf008.plugins.books.infrastructure.repositories.BookRepository;
import br.edu.ifba.inf008.plugins.loans.infrastructure.repositories.LoanRepository;

public class LoanedBooksAvailabilityProvider implements BookAvailabilityProvider {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public LoanedBooksAvailabilityProvider(BookRepository bookRepository,
            LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    public static void initialize() {
        System.out.println(
                "Provider class loader: " + LoanedBooksAvailabilityProvider.class.getClassLoader());
        System.out.println(
                "Interface class loader: " + BookAvailabilityProvider.class.getClassLoader());

        BookAvailabilityManager.getInstance().setProvider(
                new LoanedBooksAvailabilityProvider(BookRepository.getInstance(),
                        LoanRepository.getInstance()));
    }

    @Override
    public Long provideAvailableCopies(Long bookId) {
        Long totalCopies = bookRepository.findById(bookId)
                .map(Book::getCopiesAvailable)
                .map(Integer::longValue)
                .orElse(0L);

        Long loanedCopies = loanRepository.countByBookIdAndNotReturned(bookId);

        return totalCopies - loanedCopies;
    }

}

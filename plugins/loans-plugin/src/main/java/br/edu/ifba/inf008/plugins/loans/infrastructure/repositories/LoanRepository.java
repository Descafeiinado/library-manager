package br.edu.ifba.inf008.plugins.loans.infrastructure.repositories;

import br.edu.ifba.inf008.core.infrastructure.repositories.impl.HibernateRepository;
import br.edu.ifba.inf008.plugins.loans.domain.entities.Loan;

/**
 * Repository class for managing Loan entities.
 */
public class LoanRepository extends HibernateRepository<Loan, Long> {

    /**
     * Singleton instance of BookRepository.
     */
    private static final LoanRepository INSTANCE = new LoanRepository();

    protected LoanRepository() {
        super(Loan.class);
    }

    public static LoanRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Counts the number of loans for a specific book that have not been returned.
     *
     * @param bookId the ID of the book
     * @return the count of loans that have not been returned
     */
    public Long countByBookIdAndNotReturned(Long bookId) {
        try (var session = getSession()) {
            return session.createQuery(
                            "SELECT COUNT(l) FROM Loan l WHERE l.book.id = :bookId AND l.returnDate IS NULL",
                            Long.class)
                    .setParameter("bookId", bookId)
                    .uniqueResultOptional()
                    .orElse(0L);
        }
    }

    /**
     * Check if exists a loans for a specific user and book that have not been returned.
     *
     * @param userId the ID of the user
     * @param bookId the ID of the book
     * @return true if a loan exists, false otherwise
     */
    public boolean existsByUserIdAndBookIdAndNotReturned(Long userId, Long bookId) {
        try (var session = getSession()) {
            return session.createQuery(
                            "SELECT COUNT(l) > 0 FROM Loan l WHERE l.user.id = :userId AND l.book.id = :bookId AND l.returnDate IS NULL",
                            Boolean.class)
                    .setParameter("userId", userId)
                    .setParameter("bookId", bookId)
                    .uniqueResultOptional()
                    .orElse(false);
        }
    }

}

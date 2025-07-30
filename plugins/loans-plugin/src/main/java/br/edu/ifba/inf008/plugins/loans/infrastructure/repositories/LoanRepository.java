package br.edu.ifba.inf008.plugins.loans.infrastructure.repositories;

import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.repositories.impl.HibernateRepository;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.loans.application.extensions.reports.models.LoanedBookInformationModel;
import br.edu.ifba.inf008.plugins.loans.domain.entities.Loan;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository class for managing Loan entities.
 */
public class LoanRepository extends HibernateRepository<Loan, Long> {

    /**
     * Singleton instance of LoanRepository.
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
                    Long.class).setParameter("bookId", bookId).uniqueResultOptional().orElse(0L);
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
                            Boolean.class).setParameter("userId", userId).setParameter("bookId", bookId)
                    .uniqueResultOptional().orElse(false);
        }
    }

    public PageableResponse<LoanedBookInformationModel> findLoanedBooks(int page, int size) {
        try (var session = getSession()) {
            // Step 1: Get distinct book IDs with active loans, paginated
            List<Long> bookIds = session.createQuery("""
                                SELECT DISTINCT b.id
                                FROM Loan l
                                JOIN l.book b
                                WHERE l.returnDate IS NULL
                            """, Long.class).setFirstResult(page * size).setMaxResults(size)
                    .getResultList();

            if (bookIds.isEmpty()) {
                return new PageableResponse<>(page, size, 0L, List.of());
            }

            // Step 2: Load full Book entities
            List<Book> books = session.createQuery("""
                        FROM Book b
                        WHERE b.id IN :bookIds
                    """, Book.class).setParameter("bookIds", bookIds).getResultList();

            // Step 3: Count active loans per book
            Map<Long, Long> loanedCopiesMap = session.createQuery("""
                                SELECT b.id, COUNT(l)
                                FROM Loan l
                                JOIN l.book b
                                WHERE b.id IN :bookIds AND l.returnDate IS NULL
                                GROUP BY b.id
                            """, Object[].class).setParameter("bookIds", bookIds).getResultList().stream()
                    .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

            // Step 4: Build result manually
            List<LoanedBookInformationModel> data = books.stream().map(book -> {
                long totalCopies = book.getCopiesAvailable(); // assuming this field is "total"
                long loanedCopies = loanedCopiesMap.getOrDefault(book.getBookId(), 0L);
                long availableCopies = totalCopies - loanedCopies;
                return new LoanedBookInformationModel(book.getBookId(), book.getTitle(),
                        availableCopies, loanedCopies, totalCopies);
            }).toList();

            // Step 5: Total count for pagination
            long totalElements = session.createQuery("""
                        SELECT COUNT(DISTINCT b.id)
                        FROM Loan l
                        JOIN l.book b
                        WHERE l.returnDate IS NULL
                    """, Long.class).getSingleResult();

            return new PageableResponse<>(page, size, totalElements, data);
        }
    }


}

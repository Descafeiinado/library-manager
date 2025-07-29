package br.edu.ifba.inf008.plugins.loans.domain.entities;

import br.edu.ifba.inf008.core.ui.components.table.annotations.TableColumnSize;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableIgnore;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableLabel;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    public static final Long MAX_LOAN_DAYS = 7L;

    /**
     * Represents a loan in the system.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id", nullable = false, unique = true)
    @TableLabel("#")
    @TableColumnSize(50)
    private Long loanId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "loan_date", columnDefinition = "DATE NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @TableIgnore
    private LocalDate loanDate = LocalDate.now();

    @Column(name = "return_date", columnDefinition = "DATE DEFAULT NULL")
    @TableIgnore
    private LocalDate returnDate;

    public Loan() {
    }

    public Loan(User user, Book book, LocalDate loanDate, LocalDate returnDate) {
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @TableLabel("Status")
    @TableColumnSize(160)
    public String getStatus() {
        if (returnDate == null) {
            LocalDate now = LocalDate.now();

            if (loanDate != null && now.isAfter(loanDate.plusDays(MAX_LOAN_DAYS))) {
                return "Overdue " + (now.getDayOfYear() - loanDate.plusDays(MAX_LOAN_DAYS)
                        .getDayOfYear()) + " days";
            }

            return "Loaned";
        }

        LocalDate wasReturnWithinLimit = loanDate.plusDays(MAX_LOAN_DAYS);

        if (returnDate.isAfter(wasReturnWithinLimit)) {
            return "Returned late by " + (returnDate.getDayOfYear()
                    - wasReturnWithinLimit.getDayOfYear()) + " days";
        }

        return "Returned on time";
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loanId=" + loanId +
                ", user=" + user +
                ", book=" + book +
                ", loanDate=" + loanDate +
                ", returnDate=" + returnDate +
                '}';
    }

}

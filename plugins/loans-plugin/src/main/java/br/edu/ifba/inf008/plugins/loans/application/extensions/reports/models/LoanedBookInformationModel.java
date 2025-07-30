package br.edu.ifba.inf008.plugins.loans.application.extensions.reports.models;

import br.edu.ifba.inf008.core.ui.components.table.annotations.TableColumnOrientation;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableColumnSize;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableLabel;
import javafx.geometry.Pos;

public class LoanedBookInformationModel {

    @TableLabel("#")
    @TableColumnSize(50)
    private Long bookId;

    @TableLabel("Title")
    @TableColumnSize(220)
    private String bookTitle;

    @TableLabel("Available")
    @TableColumnOrientation(Pos.CENTER)
    private Long availableCopies;

    @TableLabel("Loaned")
    @TableColumnOrientation(Pos.CENTER)
    private Long loanedCopies;

    @TableLabel("Total")
    @TableColumnOrientation(Pos.CENTER)
    private Long totalCopies;

    public LoanedBookInformationModel(Long bookId, String bookTitle, Long availableCopies, Long loanedCopies, Long totalCopies) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.availableCopies = availableCopies;
        this.loanedCopies = loanedCopies;
        this.totalCopies = totalCopies;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Long getAvailableCopies() {
        return availableCopies;
    }

    public Long getLoanedCopies() {
        return loanedCopies;
    }

    public Long getTotalCopies() {
        return totalCopies;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setAvailableCopies(Long availableCopies) {
        this.availableCopies = availableCopies;
    }

    public void setLoanedCopies(Long loanedCopies) {
        this.loanedCopies = loanedCopies;
    }

    public void setTotalCopies(Long totalCopies) {
        this.totalCopies = totalCopies;
    }
}

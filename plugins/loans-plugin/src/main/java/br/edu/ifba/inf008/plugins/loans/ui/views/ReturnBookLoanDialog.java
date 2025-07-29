package br.edu.ifba.inf008.plugins.loans.ui.views;

import br.edu.ifba.inf008.core.ui.views.GenericConfirmationDialogView;
import br.edu.ifba.inf008.plugins.loans.application.services.LoanService;
import br.edu.ifba.inf008.plugins.loans.domain.entities.Loan;
import java.util.function.Consumer;

/**
 * Dialog for confirming the return of a book loan. This dialog prompts the user to confirm the
 * return of a book loan. If confirmed, it marks the loan as returned and triggers a callback if
 * provided.
 */
public class ReturnBookLoanDialog extends GenericConfirmationDialogView {

    private static final LoanService loanService = LoanService.getInstance();

    /**
     * Callback to be executed when a loan is successfully returned.
     */
    private Consumer<Loan> onLoanReturned;

    public ReturnBookLoanDialog(Loan loan) {
        super("Returning Book",
                "Are you sure you want to return the loan from '" + loan.getUser().getName()
                        + "' of the book '"
                        + loan.getBook().getTitle() + "'?");

        setOnConfirmedClick(() -> {
            try {
                loanService.markAsReturned(loan.getLoanId());

                if (onLoanReturned != null) {
                    onLoanReturned.accept(loan);
                }
            } catch (Exception e) {
                return;
            }

            close();
        });
    }

    /**
     * Sets the callback to be executed when a loan is returned.
     *
     * @param onLoanReturned the callback to set
     */
    public void setOnLoanReturned(Consumer<Loan> onLoanReturned) {
        this.onLoanReturned = onLoanReturned;
    }

}

package br.edu.ifba.inf008.plugins.loans.ui.providers;

import br.edu.ifba.inf008.core.ui.components.table.TableComponent;
import br.edu.ifba.inf008.core.ui.components.table.interfaces.TableAction;
import br.edu.ifba.inf008.plugins.loans.domain.entities.Loan;
import br.edu.ifba.inf008.plugins.loans.ui.PluginIcons;
import br.edu.ifba.inf008.plugins.loans.ui.views.ReturnBookLoanDialog;
import java.util.List;
import java.util.function.Predicate;

/**
 * Provides actions for the loan table component.
 */
public class LoanTableActionsProvider {

    /**
     * Returns a list of actions for the loan table component. Which includes view, edit, and delete
     * actions.
     *
     * @param tableComponent the table component to which the actions will be added
     * @return a list of actions for the loan table component
     */
    public static List<TableAction<Loan>> getActions(TableComponent<Loan> tableComponent) {
        return List.of(new TableAction<>() {
            public String getLabel() {
                return "Return";
            }

            public String getIconPath() {
                return PluginIcons.RETURN;
            }

            public void onAction(Loan loan) {
                ReturnBookLoanDialog dialog = new ReturnBookLoanDialog(loan);

                dialog.setOnLoanReturned(_ -> tableComponent.reload());
                dialog.showAndWait();
            }

            public Predicate<Loan> getCondition() {
                return (loan) -> loan.getReturnDate() == null;
            }
        });
    }

}

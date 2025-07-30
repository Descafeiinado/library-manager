package br.edu.ifba.inf008.plugins.loans.application.extensions.reports;

import br.edu.ifba.inf008.core.ui.components.table.TableComponent;
import br.edu.ifba.inf008.core.ui.components.table.interfaces.TableAction;
import br.edu.ifba.inf008.plugins.loans.application.extensions.reports.models.LoanedBookInformationModel;
import br.edu.ifba.inf008.plugins.loans.application.extensions.reports.views.LoansByBookDialog;
import br.edu.ifba.inf008.plugins.loans.infrastructure.repositories.LoanRepository;
import br.edu.ifba.inf008.plugins.loans.ui.PluginIcons;
import br.edu.ifba.inf008.plugins.reports.domain.entities.Report;
import java.util.List;
import java.util.function.Predicate;
import javafx.scene.Node;

public class LoanedBooksReport implements Report {

    private static final LoanRepository loanRepository = LoanRepository.getInstance();

    @Override
    public String getId() {
        return "loaned-books-report";
    }

    @Override
    public String getName() {
        return "Loaned Books Report";
    }

    @Override
    public Node getMainContent() {
        TableComponent<LoanedBookInformationModel> tableComponent = new TableComponent<>(
                LoanedBookInformationModel.class,
                loanRepository::findLoanedBooks);

        tableComponent.addActionColumn(List.of(
                new TableAction<>() {
                    @Override
                    public String getLabel() {
                        return "Get Loans";
                    }

                    @Override
                    public String getIconPath() {
                        return PluginIcons.VIEW;
                    }

                    @Override
                    public void onAction(LoanedBookInformationModel item) {
                        LoansByBookDialog loansByBookDialog = new LoansByBookDialog(item);

                        loansByBookDialog.showAndWait();
                    }

                    @Override
                    public Predicate<LoanedBookInformationModel> getCondition() {
                        return item -> item.getLoanedCopies() > 0;
                    }
                }
        ));

        return tableComponent;
    }

}

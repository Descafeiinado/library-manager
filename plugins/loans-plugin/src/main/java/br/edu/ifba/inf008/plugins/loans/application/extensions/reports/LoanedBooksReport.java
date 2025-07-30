package br.edu.ifba.inf008.plugins.loans.application.extensions.reports;

import br.edu.ifba.inf008.core.ui.components.table.TableComponent;
import br.edu.ifba.inf008.plugins.loans.application.extensions.reports.models.LoanedBookInformationModel;
import br.edu.ifba.inf008.plugins.loans.infrastructure.repositories.LoanRepository;
import br.edu.ifba.inf008.plugins.reports.domain.entities.Report;
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

        return tableComponent;
    }

}

package br.edu.ifba.inf008.plugins.loans;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IPlugin;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.domain.annotations.Plugin;
import br.edu.ifba.inf008.core.infrastructure.managers.HibernateManager;
import br.edu.ifba.inf008.core.ui.models.TabInformation;
import br.edu.ifba.inf008.plugins.loans.application.extensions.ReportsExtension;
import br.edu.ifba.inf008.plugins.loans.application.services.LoanService;
import br.edu.ifba.inf008.plugins.loans.domain.entities.Loan;
import br.edu.ifba.inf008.plugins.loans.infrastructure.providers.LoanedBooksAvailabilityProvider;
import br.edu.ifba.inf008.plugins.loans.ui.CSS;
import br.edu.ifba.inf008.plugins.loans.ui.PluginIcons;
import br.edu.ifba.inf008.plugins.loans.ui.views.MainView;

@Plugin(name = "loan-management", dependencies = {"book-management", "users-management"}, softDependencies = {"reports"})
public class LoanManagement implements IPlugin {

    private LoanService loanService;

    @Override
    public boolean init() {
        HibernateManager.registerEntityClass(Loan.class);

        loanService = LoanService.getInstance();

        return true;
    }

    @Override
    public void postInit() {
        ICore core = ICore.getInstance();
        IUIController uiController = core.getUIController();

        uiController.loadStylesheetToScene(uiController.getMainScene(), CSS.LOAN_MANAGEMENT);

        uiController.createTab(
                new TabInformation("Loans", uiController.loadIcon(PluginIcons.LOANS)),
                MainView.supply(uiController, loanService));

        LoanedBooksAvailabilityProvider.initialize();

        try {
            ReportsExtension.initialize();

            System.out.println("Reports extension initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing reports extension");
        }
    }

}

package br.edu.ifba.inf008.plugins.loans.ui.views;

import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.ui.Icons;
import br.edu.ifba.inf008.core.ui.components.table.TableComponent;
import br.edu.ifba.inf008.plugins.loans.application.services.LoanService;
import br.edu.ifba.inf008.plugins.loans.domain.entities.Loan;
import br.edu.ifba.inf008.plugins.loans.ui.providers.LoanTableActionsProvider;
import java.util.function.Supplier;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Main view for the Loans Plugin. This view displays a table of loans and provides an option to
 * create a new loan.
 */
public class MainView extends VBox {

    private final IUIController uiController;
    private final TableComponent<Loan> tableComponent;

    public MainView(IUIController uiController, LoanService loanService) {
        super(10);
        this.uiController = uiController;
        this.getStyleClass().add("lm-main-content");

        this.tableComponent = new TableComponent<>(Loan.class, loanService::findAll);
        this.tableComponent.addActionColumn(LoanTableActionsProvider.getActions(tableComponent),  45, 15, 85);

        initialize();
    }

    /**
     * Initializes the main view with a header and the loan table.
     */
    private void initialize() {
        Label titleLabel = new Label("Loans");
        titleLabel.getStyleClass().add("lm-title-label");

        Button createButton = new Button("New Loan");
        createButton.getStyleClass().add("lm-icon-button");
        createButton.setGraphic(uiController.loadIcon(Icons.PLUS));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, titleLabel, spacer, createButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("lm-header");

        configureCreateButton(createButton);

        this.getChildren().addAll(header, tableComponent);
    }

    /**
     * Configures the action for the "Create Loan" button. Opens a dialog to create a new loan and
     * reloads the table upon successful creation.
     *
     * @param createButton The button to configure.
     */
    private void configureCreateButton(Button createButton) {
        createButton.setOnAction(e -> {
            CreateLoanDialog dialog = new CreateLoanDialog();

            dialog.setOnLoanCreated((_) -> {
                tableComponent.reload();
            });
            dialog.showAndWait();
        });
    }

    /**
     * Converts this view to a Scene.
     *
     * @return A new Scene containing this view.
     */
    public Scene toScene() {
        return new Scene(this);
    }

    /**
     * Supplier method to create a new instance of MainView. This is useful for dependency injection
     * or when a Node factory is required.
     *
     * @param controller The UI controller.
     * @param service    The loan service.
     * @return A supplier that provides a new instance of MainView.
     */
    public static Supplier<Node> supply(IUIController controller, LoanService service) {
        return () -> new MainView(controller, service);
    }

}

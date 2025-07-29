package br.edu.ifba.inf008.plugins.users.ui.views;

import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.ui.Icons;
import br.edu.ifba.inf008.core.ui.components.table.TableComponent;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.application.services.UserService;
import br.edu.ifba.inf008.plugins.users.ui.providers.UserTableActionsProvider;
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
 * Main view for the Users Plugin.
 * This view displays a table of users and provides an option to create a new user.
 */
public class MainView extends VBox {

    private final IUIController uiController;
    private final TableComponent<User> tableComponent;

    public MainView(IUIController uiController, UserService userService) {
        super(10);
        this.uiController = uiController;
        this.getStyleClass().add("um-main-content");

        this.tableComponent = new TableComponent<>(User.class, userService::findAll);
        this.tableComponent.addActionColumn(UserTableActionsProvider.getActions(tableComponent));
        initialize();
    }

    /**
     * Initializes the main view with a header and the user table.
     */
    private void initialize() {
        Label titleLabel = new Label("Users");
        titleLabel.getStyleClass().add("um-title-label");

        Button createButton = new Button("New User");
        createButton.getStyleClass().add("um-icon-button");
        createButton.setGraphic(uiController.loadIcon(Icons.PLUS));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, titleLabel, spacer, createButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("um-header");

        configureCreateButton(createButton);

        this.getChildren().addAll(header, tableComponent);
    }

    /**
     * Configures the action for the "Create User" button.
     * Opens a dialog to create a new user and reloads the table upon successful creation.
     *
     * @param createButton The button to configure.
     */
    private void configureCreateButton(Button createButton) {
        createButton.setOnAction(e -> {
            CreateUserDialog dialog = new CreateUserDialog();
            dialog.setOnUserCreated(_ -> tableComponent.reload());
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
     * Supplier method to create a new instance of MainView.
     * This is useful for dependency injection or when a Node factory is required.
     *
     * @param controller The UI controller.
     * @param service The user service.
     * @return A supplier that provides a new instance of MainView.
     */
    public static Supplier<Node> supply(IUIController controller, UserService service) {
        return () -> new MainView(controller, service);
    }

}

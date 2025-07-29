package br.edu.ifba.inf008.plugins.books.ui.views;

import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.ui.Icons;
import br.edu.ifba.inf008.core.ui.components.table.TableComponent;
import br.edu.ifba.inf008.plugins.books.application.services.BookService;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.ui.providers.BookTableActionsProvider;
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
 * Main view for the Books Plugin. This view displays a table of books and provides an option to
 * create a new book.
 */
public class MainView extends VBox {

    private final IUIController uiController;
    private final TableComponent<Book> tableComponent;

    public MainView(IUIController uiController, BookService bookService) {
        super(10);
        this.uiController = uiController;
        this.getStyleClass().add("bm-main-content");

        this.tableComponent = new TableComponent<>(Book.class, bookService::findAll);
        this.tableComponent.addActionColumn(BookTableActionsProvider.getActions(tableComponent));
        initialize();
    }

    /**
     * Supplier method to create a new instance of MainView. This is useful for dependency injection
     * or when a Node factory is required.
     *
     * @param controller The UI controller.
     * @param service    The book service.
     * @return A supplier that provides a new instance of MainView.
     */
    public static Supplier<Node> supply(IUIController controller, BookService service) {
        return () -> new MainView(controller, service);
    }

    /**
     * Initializes the main view with a header and the book table.
     */
    private void initialize() {
        Label titleLabel = new Label("Books");
        titleLabel.getStyleClass().add("bm-title-label");

        Button createButton = new Button("New Book");
        createButton.getStyleClass().add("bm-icon-button");
        createButton.setGraphic(uiController.loadIcon(Icons.PLUS));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, titleLabel, spacer, createButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("bm-header");

        configureCreateButton(createButton);

        this.getChildren().addAll(header, tableComponent);
    }

    /**
     * Configures the action for the "Create Book" button. Opens a dialog to create a new book and
     * reloads the table upon successful creation.
     *
     * @param createButton The button to configure.
     */
    private void configureCreateButton(Button createButton) {
        createButton.setOnAction(e -> {
            CreateBookDialog dialog = new CreateBookDialog();

            dialog.setOnBookCreated(_ -> tableComponent.reload());
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

}

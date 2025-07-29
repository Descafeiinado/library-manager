package br.edu.ifba.inf008.core.ui.components.table;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableIgnore;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableLabel;
import br.edu.ifba.inf008.core.ui.components.table.factories.TableColumnFactory;
import br.edu.ifba.inf008.core.ui.components.table.interfaces.TableAction;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A generic table component for displaying paginated data. It supports dynamic column creation
 * based on the provided class type, and allows for pagination through next and previous buttons.
 *
 * @param <T> The type of data to be displayed in the table.
 */
public class TableComponent<T> extends VBox {

    private static final ICore core = ICore.getInstance();
    private static final IUIController uiController = core.getUIController();

    private final TableView<T> tableView = new TableView<>();
    private final Label pageInfo = new Label();
    private final Button prevButton = new Button("← Previous");
    private final Button nextButton = new Button("Next →");

    private final Class<T> clazz;
    private final BiFunction<Integer, Integer, PageableResponse<T>> loader;

    private int currentPage = 0;
    private int pageSize = 10;
    private long totalElements = 0;

    public TableComponent(Class<T> clazz,
            BiFunction<Integer, Integer, PageableResponse<T>> loader) {
        this.clazz = clazz;
        this.loader = loader;

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setSpacing(10);
        setPadding(new Insets(10));
        getStyleClass().add("table-container");

        createColumnsFromClass();
        loadPage(0);

        HBox pagination = new HBox(10, prevButton, pageInfo, nextButton);
        pagination.setAlignment(Pos.CENTER);
        pagination.setPadding(new Insets(10));
        pagination.getStyleClass().add("pagination-bar");

        prevButton.setOnAction(e -> loadPage(currentPage - 1));
        nextButton.setOnAction(e -> loadPage(currentPage + 1));

        prevButton.getStyleClass().add("table-button");
        nextButton.getStyleClass().add("table-button");

        BorderPane tableWrapper = new BorderPane();
        tableWrapper.setCenter(tableView);
        tableWrapper.setBottom(pagination);

        getChildren().add(tableWrapper);
    }

    /**
     * Loads the specified page of data into the table.
     *
     * @param page The page number to load.
     */
    private void loadPage(int page) {
        if (page < 0) {
            return;
        }

        PageableResponse<T> response = loader.apply(page, pageSize);
        if (response == null) {
            return;
        }

        currentPage = response.getPage();
        pageSize = response.getSize();
        totalElements = response.getTotalElements();

        Collection<T> content = response.getContent();
        tableView.setItems(FXCollections.observableArrayList(content));

        updatePaginationState();
    }

    /**
     * Updates the pagination state based on the current page and total elements. It updates the
     * page information label and enables/disables the navigation buttons.
     */
    private void updatePaginationState() {
        long totalPages = (long) Math.ceil((double) totalElements / pageSize);

        pageInfo.setText("Page " + (currentPage + 1) + " of " + Math.max(totalPages, 1));
        prevButton.setDisable(currentPage <= 0);
        nextButton.setDisable(currentPage >= totalPages - 1);
    }

    /**
     * Reloads the current page of data in the table. This method can be used to refresh the data
     * displayed in the table.
     */
    public void reload() {
        loadPage(currentPage);
    }

    /**
     * Creates table columns based on the fields of the specified class. It will skip fields
     * annotated with @TableIgnore and use @TableLabel for custom headers.
     */
    private void createColumnsFromClass() {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableIgnore.class) || Modifier.isStatic(
                    field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);

            String headerName = capitalize(field.getName());

            if (field.isAnnotationPresent(TableLabel.class)) {
                headerName = field.getAnnotation(TableLabel.class).value();
            }

            TableColumn<T, String> column = TableColumnFactory.getTStringTableColumn(field,
                    headerName);
            tableView.getColumns().add(column);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(TableLabel.class) || Modifier.isStatic(
                    method.getModifiers()) || !method.getReturnType().equals(String.class)
                    || method.getParameterCount() != 0) {
                continue;
            }

            TableColumn<T, String> column = TableColumnFactory.getTStringTableColumn(method);

            tableView.getColumns().add(column);
        }
    }

    /**
     * Adds a custom action column to the table with a specified label. The column will display a
     * label for each row and show a tooltip with the full text.
     *
     * @param column The TableColumn to add as an action column.
     */
    public void addActionColumn(TableColumn<T, String> column) {
        column.setCellFactory(col -> new TableCell<>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                } else {
                    label.setText(item);

                    Tooltip tooltip = new Tooltip(item);
                    Tooltip.install(label, tooltip);

                    setGraphic(label);
                }
            }
        });

        tableView.getColumns().add(column);
    }

    /**
     * Adds a custom action column to the table with a list of actions. Each action will be
     * represented by a button in the action column.
     *
     * @param actions The list of TableAction objects to be added as buttons in the action column.
     */
    public void addActionColumn(List<TableAction<T>> actions) {
        addActionColumn(actions, 120, 90, 160);
    }

    /**
     * Adds a custom action column to the table with a list of actions. Each action will be
     * represented by a button in the action column.
     *
     * @param actions   The list of TableAction objects to be added as buttons in the action
     *                  column.
     * @param prefWidth The preferred width of the action column.
     * @param minWidth  The minimum width of the action column.
     * @param maxWidth  The maximum width of the action column.
     */
    public void addActionColumn(List<TableAction<T>> actions, double prefWidth, double minWidth,
            double maxWidth) {
        TableColumn<T, Void> actionCol = new TableColumn<>("Actions");

        actionCol.setPrefWidth(prefWidth);
        actionCol.setMinWidth(minWidth);
        actionCol.setMaxWidth(maxWidth);

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final HBox container = new HBox(2);

            {
                container.setAlignment(Pos.CENTER);
                container.setPadding(new Insets(0));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                container.getChildren().clear();

                if (!empty) {
                    T rowData = getTableView().getItems().get(getIndex());
                    for (TableAction<T> action : actions) {
                        if (!action.getCondition().test(rowData)) {
                            continue;
                        }

                        Button button = new Button();

                        button.setGraphic(uiController.loadIcon(action.getIconPath()));
                        button.setTooltip(new Tooltip(action.getLabel()));
                        button.setOnAction(e -> action.onAction(rowData));
                        button.setMinSize(24, 24);
                        button.setMaxSize(24, 24);

                        button.getStyleClass().add("table-action-button");

                        container.getChildren().add(button);
                    }

                    setGraphic(container);
                } else {
                    setGraphic(null);
                }
            }
        });

        tableView.getColumns().add(actionCol);
    }

    /**
     * Capitalizes the first letter of the given text.
     *
     * @param text The text to capitalize.
     * @return The capitalized text.
     */
    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * Returns the TableView associated with this TableComponent.
     *
     * @return The TableView instance.
     */
    public TableView<T> getTableView() {
        return tableView;
    }

    /**
     * Returns the current node of this TableComponent. This method is used to retrieve the Node
     * representation of this component, which can be useful for adding it to a Scene or another
     * layout.
     *
     * @return The Node representing this TableComponent.
     */
    public Node getNode() {
        return this;
    }

}

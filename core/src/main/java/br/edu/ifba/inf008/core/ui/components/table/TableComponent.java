package br.edu.ifba.inf008.core.ui.components.table;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableColumnSize;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableIgnore;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableLabel;
import br.edu.ifba.inf008.core.ui.components.table.interfaces.TableAction;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import javafx.beans.property.SimpleStringProperty;
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
 * A generic table component for displaying paginated data.
 * It supports dynamic column creation based on the provided class type,
 * and allows for pagination through next and previous buttons.
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
     * Creates a TableColumn for a field of type String, LocalDateTime, LocalDate, or Date.
     * The column will display the field's value and handle tooltips for long text.
     *
     * @param field The field to create the column for.
     * @param headerName The header name for the column.
     * @param <T> The type of the table row.
     * @return A TableColumn configured for the specified field.
     */
    private static <T> TableColumn<T, String> getTStringTableColumn(Field field,
            String headerName) {
        TableColumn<T, String> column = new TableColumn<>(headerName);
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

        column.setCellValueFactory(cellData -> {
            try {
                Object value = field.get(cellData.getValue());

                return switch (value) {
                    case null -> new SimpleStringProperty("");
                    case LocalDateTime dateTime -> new SimpleStringProperty(
                            dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    case LocalDate date -> new SimpleStringProperty(
                            date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    case Date date -> new SimpleStringProperty(
                            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date));
                    default -> new SimpleStringProperty(value.toString());
                };

            } catch (IllegalAccessException e) {
                return new SimpleStringProperty("");
            }
        });

        double prefWidth = 150;

        if (field.isAnnotationPresent(TableColumnSize.class)) {
            prefWidth = field.getAnnotation(TableColumnSize.class).value();
        }

        column.setMinWidth(prefWidth * 0.8);
        column.setPrefWidth(prefWidth);
        column.setMaxWidth(prefWidth * 1.2);

        return column;
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
     * Updates the pagination state based on the current page and total elements.
     * It updates the page information label and enables/disables the navigation buttons.
     */
    private void updatePaginationState() {
        long totalPages = (long) Math.ceil((double) totalElements / pageSize);

        pageInfo.setText("Page " + (currentPage + 1) + " of " + Math.max(totalPages, 1));
        prevButton.setDisable(currentPage <= 0);
        nextButton.setDisable(currentPage >= totalPages - 1);
    }

    /**
     * Reloads the current page of data in the table.
     * This method can be used to refresh the data displayed in the table.
     */
    public void reload() {
        loadPage(currentPage);
    }

    /**
     * Creates table columns based on the fields of the specified class.
     * It will skip fields annotated with @TableIgnore and use @TableLabel for custom headers.
     */
    private void createColumnsFromClass() {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableIgnore.class)) {
                continue;
            }

            field.setAccessible(true);

            String headerName = capitalize(field.getName());

            if (field.isAnnotationPresent(TableLabel.class)) {
                headerName = field.getAnnotation(TableLabel.class).value();
            }

            TableColumn<T, String> column = getTStringTableColumn(field, headerName);
            tableView.getColumns().add(column);
        }
    }

    /**
     * Adds a custom action column to the table with a specified label.
     * The column will display a label for each row and show a tooltip with the full text.
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
     * Adds a custom action column to the table with a list of actions.
     * Each action will be represented by a button in the action column.
     *
     * @param actions The list of TableAction objects to be added as buttons in the action column.
     */
    public void addActionColumn(List<TableAction<T>> actions) {
        TableColumn<T, Void> actionCol = new TableColumn<>("Actions");

        actionCol.setPrefWidth(120);
        actionCol.setMinWidth(90);
        actionCol.setMaxWidth(160);

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final HBox container = new HBox(2);

            {
                container.setAlignment(Pos.CENTER_LEFT);
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

                        button.getStyleClass().add("table-action-button"); // já estilizado

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
     * Returns the current node of this TableComponent.
     * This method is used to retrieve the Node representation of this component,
     * which can be useful for adding it to a Scene or another layout.
     * @return The Node representing this TableComponent.
     */
    public Node getNode() {
        return this;
    }

}

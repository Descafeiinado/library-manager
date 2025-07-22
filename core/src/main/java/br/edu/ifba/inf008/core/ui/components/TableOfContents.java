package br.edu.ifba.inf008.core.ui.components;

import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.ui.annotations.TableIgnore;
import br.edu.ifba.inf008.core.ui.models.TableAction;
import java.lang.reflect.Field;
import java.util.Collection;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TableOfContents<T> extends VBox {

    private final TableView<T> tableView = new TableView<>();
    private final Label pageInfo = new Label();
    private final Button prevButton = new Button("← Previous");
    private final Button nextButton = new Button("Next →");

    private final Class<T> clazz;
    private final BiFunction<Integer, Integer, PageableResponse<T>> loader;

    private int currentPage = 0;
    private int pageSize = 10;
    private long totalElements = 0;

    public TableOfContents(Class<T> clazz,
            BiFunction<Integer, Integer, PageableResponse<T>> loader) {
        this.clazz = clazz;
        this.loader = loader;

        setSpacing(10);
        setPadding(new Insets(10));

        createColumnsFromClass();
        loadPage(0);

        HBox pagination = new HBox(10, prevButton, pageInfo, nextButton);
        pagination.setAlignment(Pos.CENTER);
        pagination.setPadding(new Insets(10));

        prevButton.setOnAction(e -> loadPage(currentPage - 1));
        nextButton.setOnAction(e -> loadPage(currentPage + 1));

        prevButton.getStyleClass().add("table-button");
        nextButton.getStyleClass().add("table-button");

        BorderPane tableWrapper = new BorderPane();
        tableWrapper.setCenter(tableView);
        tableWrapper.setBottom(pagination);

        getChildren().add(tableWrapper);
    }

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

    private void updatePaginationState() {
        long totalPages = (long) Math.ceil((double) totalElements / pageSize);

        pageInfo.setText("Page " + (currentPage + 1) + " of " + Math.max(totalPages, 1));
        prevButton.setDisable(currentPage <= 0);
        nextButton.setDisable(currentPage >= totalPages - 1);
    }

    private void createColumnsFromClass() {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableIgnore.class)) {
                continue;
            }

            field.setAccessible(true);

            TableColumn<T, String> column = new TableColumn<>(capitalize(field.getName()));
            column.setCellValueFactory(cellData -> {
                try {
                    Object value = field.get(cellData.getValue());
                    return new SimpleStringProperty(value != null ? value.toString() : "");
                } catch (IllegalAccessException e) {
                    return new SimpleStringProperty("");
                }
            });

            column.setPrefWidth(150);
            tableView.getColumns().add(column);
        }
    }

    public void addActionColumn(List<TableAction<T>> actions) {
        TableColumn<T, Void> actionCol = new TableColumn<>("Actions");

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final HBox container = new HBox(5);

            {
                container.setAlignment(Pos.CENTER_LEFT);
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
                        button.setGraphic(new ImageView(
                                new Image(getClass().getResourceAsStream(action.getIconPath()), 16,
                                        16, true, true)));
                        button.setTooltip(new Tooltip(action.getLabel()));
                        button.setOnAction(e -> action.onAction(rowData));
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


    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public TableView<T> getTableView() {
        return tableView;
    }

    public Node getNode() {
        return this;
    }
}

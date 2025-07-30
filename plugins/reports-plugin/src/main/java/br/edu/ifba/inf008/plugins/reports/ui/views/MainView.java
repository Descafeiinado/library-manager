package br.edu.ifba.inf008.plugins.reports.ui.views;

import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.plugins.reports.domain.entities.Report;
import br.edu.ifba.inf008.plugins.reports.infrastructure.repositories.ReportsRepository;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Main view for the Reports Plugin. This view displays a ComboBox to select a report and renders its
 * content below.
 */
public class MainView extends BorderPane {

    private final IUIController uiController;
    private final ReportsRepository reportsRepository;

    private ComboBox<Report> reportComboBox;
    private StackPane contentArea;

    public MainView(IUIController uiController) {
        this.uiController = uiController;
        this.reportsRepository = ReportsRepository.getInstance();
        this.getStyleClass().add("rp-main-content");

        initialize();
    }

    /**
     * Supplier method to create a new instance of MainView. This is useful for dependency injection
     * or when a Node factory is required.
     *
     * @param controller The UI controller.
     * @return A supplier that provides a new instance of MainView.
     */
    public static Supplier<Node> supply(IUIController controller) {
        return () -> new MainView(controller);
    }

    /**
     * Initializes the main view with a header, a report ComboBox, and a content area.
     */
    private void initialize() {
        Label titleLabel = new Label("Reports");
        titleLabel.getStyleClass().add("rp-title-label");
        HBox header = new HBox(10, titleLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("rp-header");
        this.setTop(header);

        List<Report> reports = reportsRepository.findAll();
        this.reportComboBox = createComboBox(reports, Report::getName);
        reportComboBox.setPromptText("Select a report...");
        reportComboBox.setMaxWidth(Double.MAX_VALUE);

        this.contentArea = new StackPane();
        contentArea.getStyleClass().add("rp-content-area");

        VBox mainLayout = new VBox(10, reportComboBox, contentArea);
        mainLayout.setPadding(new Insets(10));
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        this.setCenter(mainLayout);

        addSelectionListener();
        selectFirstReport(reports);
    }

    /**
     * Adds a listener to the ComboBox to display the content of the selected report.
     */
    private void addSelectionListener() {
        reportComboBox.valueProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        displayReportContent(newSelection);
                    } else {
                        contentArea.getChildren().clear();
                    }
                });
    }

    /**
     * Selects the first report in the list by default.
     *
     * @param reports The list of available reports.
     */
    private void selectFirstReport(List<Report> reports) {
        if (!reports.isEmpty()) {
            reportComboBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Clears the content area and displays the main content Node of the given report.
     *
     * @param report The report to display.
     */
    private void displayReportContent(Report report) {
        contentArea.getChildren().clear();
        Node reportNode = report.getMainContent();
        if (reportNode != null) {
            contentArea.getChildren().add(reportNode);
        }
    }

    /**
     * Creates a standard, non-editable ComboBox.
     *
     * @param items        The list of items to display in the ComboBox.
     * @param stringMapper A function to map an item object to its string representation for
     * display.
     * @param <T>          The type of the items.
     * @return A configured ComboBox.
     */
    private <T> ComboBox<T> createComboBox(List<T> items, Function<T, String> stringMapper) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(items));

        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : stringMapper.apply(object);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });

        return comboBox;
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
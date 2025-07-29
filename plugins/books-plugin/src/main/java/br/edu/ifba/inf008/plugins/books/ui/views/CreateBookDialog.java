package br.edu.ifba.inf008.plugins.books.ui.views;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.domain.exceptions.IsbnAlreadyExistingException;
import br.edu.ifba.inf008.plugins.books.application.services.BookService;
import br.edu.ifba.inf008.plugins.books.infrastructure.models.request.CreateBookRequest;
import br.edu.ifba.inf008.plugins.books.ui.CSS;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Dialog for creating a new book.
 * This dialog allows the user to input book details such as ISBN, title, author,
 * published year, and copies available. And then handles validation and error display.
 */
public class CreateBookDialog extends Stage {

    private static final IUIController uiController = ICore.getInstance().getUIController();
    private static final BookService BOOK_SERVICE = BookService.getInstance();

    private final TextField isbnField = new TextField();
    private final TextField titleField = new TextField();
    private final TextField authorField = new TextField();
    private final ComboBox<Integer> publishedYearField = new ComboBox<>();
    private final Slider copiesAvailableField = new Slider(0, 50, 0);

    private final Label copiesAvailableLabel = new Label("0");

    private final Label isbnErrorLabel = new Label();
    private final Label titleErrorLabel = new Label();
    private final Label authorErrorLabel = new Label();
    private final Label publishedYearErrorLabel = new Label();
    private final Label copiesAvailableErrorLabel = new Label();

    private Consumer<Book> onBookCreated;

    public CreateBookDialog() {
        setTitle("Creating Book");
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);

        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear; year >= 1900; year--) {
            publishedYearField.getItems().add(year);
        }
        publishedYearField.setEditable(false);
        publishedYearField.setPromptText("Select Year");

        VBox formLayout = new VBox(15);
        formLayout.setPadding(new Insets(20));
        formLayout.setPrefWidth(350);
        formLayout.getStyleClass().add("bm-form-container");

        Label titleLabel = new Label("New Book");
        titleLabel.getStyleClass().add("bm-form-title");
        titleLabel.setStyle("-fx-padding: 0 0 12 0;");

        isbnField.setPromptText("ISBN");
        titleField.setPromptText("Title");
        authorField.setPromptText("Author");
        publishedYearField.setPromptText("Publish Date");
        publishedYearField.getStyleClass().add("bm-combobox");

        isbnErrorLabel.getStyleClass().add("bm-error-label");
        titleErrorLabel.getStyleClass().add("bm-error-label");
        authorErrorLabel.getStyleClass().add("bm-error-label");
        publishedYearErrorLabel.getStyleClass().add("bm-error-label");
        copiesAvailableErrorLabel.getStyleClass().add("bm-error-label");

        copiesAvailableField.setShowTickLabels(true);
        copiesAvailableField.setShowTickMarks(true);
        copiesAvailableField.setMajorTickUnit(20);
        copiesAvailableField.setMinorTickCount(4);
        copiesAvailableField.setBlockIncrement(1);
        copiesAvailableField.valueProperty().addListener((obs, oldVal, newVal) -> {
            copiesAvailableLabel.setText(String.valueOf(newVal.intValue()));
        });
        copiesAvailableField.getStyleClass().add("bm-slider");
        copiesAvailableLabel.getStyleClass().add("bm-slider-label");

        Button cancelButton = new Button("Cancel");
        Button createButton = new Button("Create");

        cancelButton.getStyleClass().add("bm-cancel-button");
        createButton.getStyleClass().add("bm-save-button");

        cancelButton.setOnAction(e -> close());

        createButton.setOnAction(e -> {
            clearErrors();

            String isbn = isbnField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            Integer publishedYear = publishedYearField.getValue();
            int copies = (int) copiesAvailableField.getValue();

            boolean valid = true;

            if (isbn.isEmpty()) {
                isbnErrorLabel.setText("ISBN is required.");
                isbnField.getStyleClass().add("bm-field-error");
                valid = false;
            }

            if (title.isEmpty()) {
                titleErrorLabel.setText("Title is required.");
                titleField.getStyleClass().add("bm-field-error");
                valid = false;
            }

            if (author.isEmpty()) {
                authorErrorLabel.setText("Author is required.");
                authorField.getStyleClass().add("bm-field-error");
                valid = false;
            }

            if (publishedYear == null) {
                publishedYearErrorLabel.setText("Publish year is required.");
                publishedYearField.getStyleClass().add("bm-field-error");
                valid = false;
            }

            if (!valid) return;

            try {
                CreateBookRequest request = new CreateBookRequest(
                        isbn, title, author, publishedYear, copies
                );

                Book result = BOOK_SERVICE.create(request);

                if (onBookCreated != null) {
                    onBookCreated.accept(result);
                }

                close();

            } catch (IsbnAlreadyExistingException ex) {
                isbnErrorLabel.setText("This ISBN is already in use.");
                isbnField.getStyleClass().add("bm-field-error");
            } catch (ConstraintViolationException ex) {
                ex.getConstraintViolations().forEach(violation -> {
                    String path = violation.getPropertyPath().toString();
                    String message = violation.getMessage();

                    if (path.contains("isbn")) {
                        isbnErrorLabel.setText(message);
                        isbnField.getStyleClass().add("bm-field-error");
                    } else if (path.contains("title")) {
                        titleErrorLabel.setText(message);
                        titleField.getStyleClass().add("bm-field-error");
                    } else if (path.contains("author")) {
                        authorErrorLabel.setText(message);
                        authorField.getStyleClass().add("bm-field-error");
                    } else if (path.contains("publishDate")) {
                        publishedYearErrorLabel.setText(message);
                        publishedYearField.getStyleClass().add("bm-field-error");
                    } else if (path.contains("copiesAvailable")) {
                        copiesAvailableErrorLabel.setText(message);
                        copiesAvailableField.getStyleClass().add("bm-field-error");
                    }
                });
            }
        });

        HBox copiesRow = new HBox(10, copiesAvailableField, copiesAvailableLabel);
        copiesRow.setAlignment(Pos.CENTER_LEFT);

        HBox buttonRow = new HBox(10, cancelButton, createButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        formLayout.getChildren().addAll(
                titleLabel,
                createLabeledField("ISBN", isbnField, isbnErrorLabel),
                createLabeledField("Title", titleField, titleErrorLabel),
                createLabeledField("Author", authorField, authorErrorLabel),
                createLabeledField("Publish Year", publishedYearField, publishedYearErrorLabel),
                createLabeledField("Total Copies Available", copiesRow, copiesAvailableErrorLabel),
                buttonRow
        );

        Scene scene = new Scene(formLayout);
        uiController.loadStylesheetToScene(scene, CSS.BOOKS_MANAGEMENT);
        setScene(scene);
    }

    public void setOnBookCreated(Consumer<Book> onBookCreated) {
        this.onBookCreated = onBookCreated;
    }

    private VBox createLabeledField(String labelText, javafx.scene.Node field, Label errorLabel) {
        Label label = new Label(labelText);
        errorLabel.setWrapText(true);
        VBox box = new VBox(label, field, errorLabel);
        box.setSpacing(1);
        box.setPadding(new Insets(0, 0, 6, 0));
        box.getStyleClass().add("bm-labeled-field");
        return box;
    }

    private void clearErrors() {
        isbnErrorLabel.setText("");
        titleErrorLabel.setText("");
        authorErrorLabel.setText("");
        publishedYearErrorLabel.setText("");
        copiesAvailableErrorLabel.setText("");

        isbnField.getStyleClass().remove("bm-field-error");
        titleField.getStyleClass().remove("bm-field-error");
        authorField.getStyleClass().remove("bm-field-error");
        publishedYearField.getStyleClass().remove("bm-field-error");
        copiesAvailableField.getStyleClass().remove("bm-field-error");
    }

}
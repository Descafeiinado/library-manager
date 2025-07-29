package br.edu.ifba.inf008.plugins.loans.ui.views;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.domain.exceptions.BookNotFoundException;
import br.edu.ifba.inf008.plugins.books.infrastructure.repositories.BookRepository;
import br.edu.ifba.inf008.plugins.loans.application.services.LoanService;
import br.edu.ifba.inf008.plugins.loans.domain.entities.Loan;
import br.edu.ifba.inf008.plugins.loans.domain.exceptions.BookNotAvailableToLoanException;
import br.edu.ifba.inf008.plugins.loans.domain.exceptions.UserAlreadyLoanedBookException;
import br.edu.ifba.inf008.plugins.loans.infrastructure.models.request.CreateLoanRequest;
import br.edu.ifba.inf008.plugins.loans.ui.CSS;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.domain.exceptions.UserNotFoundException;
import br.edu.ifba.inf008.plugins.users.infrastructure.repositories.UserRepository;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class CreateLoanDialog extends Stage {

    private static final IUIController uiController = ICore.getInstance().getUIController();

    private final LoanService loanService = LoanService.getInstance();

    private final ComboBox<User> userField;
    private final ComboBox<Book> bookField;

    private final Label userErrorLabel = new Label();
    private final Label bookErrorLabel = new Label();

    private Consumer<Loan> onLoanCreated;

    public CreateLoanDialog() {
        setTitle("Create Loan");
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);

        List<User> users = UserRepository.getInstance().findAll();
        List<Book> books = BookRepository.getInstance().findAll();

        userField = createSearchableComboBox(users, User::getName);
        bookField = createSearchableComboBox(books,
                book -> book.getTitle() + " (" + book.getIsbn() + ")");

        userField.setPromptText("Select User");
        bookField.setPromptText("Select Book");

        userErrorLabel.getStyleClass().add("lm-error-label");
        bookErrorLabel.getStyleClass().add("lm-error-label");

        Button createButton = new Button("Create");
        createButton.getStyleClass().add("lm-save-button");

        createButton.setOnAction(e -> {
            clearErrors();

            User user = userField.getValue();
            Book book = bookField.getValue();

            boolean valid = true;

            if (user == null) {
                userErrorLabel.setText("User is required.");
                userField.getStyleClass().add("lm-field-error");
                valid = false;
            }

            if (book == null) {
                bookErrorLabel.setText("Book is required.");
                bookField.getStyleClass().add("lm-field-error");
                valid = false;
            }

            if (!valid) {
                return;
            }

            try {
                Loan loan = loanService.create(
                        new CreateLoanRequest(user.getUserId(), book.getBookId()));

                if (onLoanCreated != null) {
                    onLoanCreated.accept(loan);
                }

                close();
            } catch (UserNotFoundException | UserAlreadyLoanedBookException ex) {
                userErrorLabel.setText(ex.getMessage());
                userField.getStyleClass().add("lm-field-error");
            } catch (BookNotFoundException | BookNotAvailableToLoanException ex) {
                bookErrorLabel.setText(ex.getMessage());
                bookField.getStyleClass().add("lm-field-error");
            } catch (ConstraintViolationException ex) {
                ex.getConstraintViolations().forEach(violation -> {
                    String path = violation.getPropertyPath().toString();
                    String message = violation.getMessage();

                    if (path.contains("userId")) {
                        userErrorLabel.setText(message);
                        userField.getStyleClass().add("lm-field-error");
                    } else if (path.contains("bookId")) {
                        bookErrorLabel.setText(message);
                        bookField.getStyleClass().add("lm-field-error");
                    }
                });
            }
        });

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setPrefWidth(400);
        layout.setMaxWidth(400);
        layout.setMinWidth(400);
        layout.getStyleClass().add("lm-form-container");

        layout.getChildren().addAll(createLabeledField("User", userField, userErrorLabel),
                createLabeledField("Book", bookField, bookErrorLabel), createButton);

        Scene scene = new Scene(layout);
        uiController.loadStylesheetToScene(scene, CSS.LOAN_MANAGEMENT);
        setScene(scene);
    }

    public void setOnLoanCreated(Consumer<Loan> onLoanCreated) {
        this.onLoanCreated = onLoanCreated;
    }

    private VBox createLabeledField(String labelText, Node field, Label errorLabel) {
        Label label = new Label(labelText);

        errorLabel.setWrapText(true);
        errorLabel.setTextOverrun(OverrunStyle.CLIP);

        errorLabel.setMaxWidth(350);
        errorLabel.setPrefWidth(350);
        errorLabel.setMinWidth(Region.USE_PREF_SIZE);
        errorLabel.setMaxHeight(Double.MAX_VALUE);
        errorLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
        errorLabel.setMinHeight(32);

        VBox box = new VBox(label, field, errorLabel);
        box.setSpacing(1);
        box.setPadding(new Insets(0, 0, 6, 0));
        box.getStyleClass().add("lm-labeled-field");

        box.setMaxWidth(Double.MAX_VALUE);

        VBox.setVgrow(errorLabel, Priority.NEVER);
        return box;
    }

    private <T> ComboBox<T> createSearchableComboBox(List<T> items,
            Function<T, String> stringMapper) {
        ComboBox<T> comboBox = new ComboBox<>();
        FilteredList<T> filteredItems = new FilteredList<>(FXCollections.observableArrayList(items),
                p -> true);
        comboBox.setItems(filteredItems);

        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : stringMapper.apply(object);
            }

            @Override
            public T fromString(String string) {
                return comboBox.getItems().stream()
                        .filter(item -> stringMapper.apply(item).equals(string)).findFirst()
                        .orElse(null);
            }
        });

        comboBox.setEditable(true);

        comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final T selected = comboBox.getSelectionModel().getSelectedItem();
            if (selected != null && stringMapper.apply(selected).equals(newValue)) {
                return;
            }

            filteredItems.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return stringMapper.apply(item).toLowerCase().contains(newValue.toLowerCase());
            });
        });

        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                if (newVal != null) {
                    comboBox.getEditor().setText(stringMapper.apply(newVal));
                    filteredItems.setPredicate(item -> true);
                }
            });
        });

        return comboBox;
    }

    private void clearErrors() {
        userErrorLabel.setText("");
        bookErrorLabel.setText("");
        userField.getStyleClass().remove("lm-field-error");
        bookField.getStyleClass().remove("lm-field-error");
    }
}

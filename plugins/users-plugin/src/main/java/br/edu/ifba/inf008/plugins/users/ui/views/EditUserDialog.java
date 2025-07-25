package br.edu.ifba.inf008.plugins.users.ui.views;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.domain.exceptions.EmailAlreadyExistingException;
import br.edu.ifba.inf008.plugins.users.domain.exceptions.UserNotFoundException;
import br.edu.ifba.inf008.plugins.users.infrastructure.models.request.EditUserRequest;
import br.edu.ifba.inf008.plugins.users.infrastructure.services.UserService;
import br.edu.ifba.inf008.plugins.users.ui.CSS;
import jakarta.validation.ConstraintViolationException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EditUserDialog extends Stage {

    private static final IUIController uiController = ICore.getInstance().getUIController();
    private static final UserService userService = UserService.getInstance();

    private final TextField nameField = new TextField();
    private final TextField emailField = new TextField();

    private final Label nameErrorLabel = new Label();
    private final Label emailErrorLabel = new Label();

    private Runnable onUserEdited;

    public EditUserDialog(User user) {
        setTitle("Editing User");

        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);

        VBox formLayout = new VBox(15);

        formLayout.setPadding(new Insets(20));
        formLayout.setPrefWidth(350);
        formLayout.getStyleClass().add("um-form-container");

        Label titleLabel = new Label("Editing: " + user.getName());
        titleLabel.setStyle("-fx-padding: 0 0 12 0;");
        titleLabel.setWrapText(true);
        titleLabel.getStyleClass().add("um-form-title");

        nameField.setPromptText("Full name");
        emailField.setPromptText("Email address");

        nameField.setText(user.getName());
        emailField.setText(user.getEmail());

        nameErrorLabel.getStyleClass().add("um-error-label");
        emailErrorLabel.getStyleClass().add("um-error-label");

        Button cancelButton = new Button("Cancel");
        Button saveButton = new Button("Save");

        HBox buttonRow = new HBox(10, cancelButton, saveButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        formLayout.getChildren().addAll(
                titleLabel,
                createLabeledField("Email", emailField, emailErrorLabel),
                createLabeledField("Name", nameField, nameErrorLabel),
                buttonRow
        );

        cancelButton.getStyleClass().add("um-cancel-button");
        saveButton.getStyleClass().add("um-save-button");

        cancelButton.setOnAction(e -> close());

        saveButton.setOnAction(e -> {
            clearErrors();

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            boolean valid = true;

            if (name.isEmpty()) {
                nameErrorLabel.setText("Name is required.");
                nameField.getStyleClass().add("um-field-error");
                valid = false;
            }

            if (email.isEmpty()) {
                emailErrorLabel.setText("Email is required.");
                emailField.getStyleClass().add("um-field-error");
                valid = false;
            }

            if (!valid) {
                return;
            }

            try {
                userService.edit(user.getUserId(), new EditUserRequest(email, name));

                if (onUserEdited != null) {
                    onUserEdited.run();
                }

                close();
            } catch (EmailAlreadyExistingException exception) {
                emailErrorLabel.setText("This email is already in use.");

                emailField.getStyleClass().add("um-field-error");
            } catch (UserNotFoundException exception) {
                // This should not happen
                close();
            } catch (ConstraintViolationException exception) {
                exception.getConstraintViolations().forEach(violation -> {
                    String path = violation.getPropertyPath().toString();
                    String message = violation.getMessage();

                    if (path.contains("name")) {
                        nameErrorLabel.setText(message);
                        nameField.getStyleClass().add("um-field-error");
                    } else if (path.contains("email")) {
                        emailErrorLabel.setText(message);
                        emailField.getStyleClass().add("um-field-error");
                    }
                });
            }
        });

        Scene scene = new Scene(formLayout);
        uiController.loadStylesheetToScene(scene, CSS.USERS_MANAGEMENT);
        setScene(scene);
    }

    public void setOnUserEdited(Runnable onUserEdited) {
        this.onUserEdited = onUserEdited;
    }

    private VBox createLabeledField(String labelText, TextField field, Label errorLabel) {
        Label label = new Label(labelText);
        errorLabel.setWrapText(true);

        VBox box = new VBox(label, field, errorLabel);
        box.setSpacing(1);
        box.setPadding(new Insets(0, 0, 6, 0));

        box.getStyleClass().add("um-labeled-field");

        return box;
    }

    private void clearErrors() {
        nameErrorLabel.setText("");
        emailErrorLabel.setText("");
        nameField.getStyleClass().remove("um-field-error");
        emailField.getStyleClass().remove("um-field-error");
    }

}

package br.edu.ifba.inf008.plugins.users.ui.views;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.ui.CSS;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Dialog for viewing user identity details.
 * This dialog displays the user's name, email, and registration date,
 * along with a circular photo placeholder.
 */
public class ViewUserDialog extends Stage {

    private static final IUIController uiController = ICore.getInstance().getUIController();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "dd/MM/yyyy HH:mm");

    public ViewUserDialog(User user) {
        setTitle("User Identity");

        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(300);
        root.getStyleClass().add("um-id-card");

        StackPane photoPane = new StackPane();
        photoPane.setPrefSize(80, 80);

        Circle circle = new Circle(40);
        circle.setFill(Color.web("#0066cc"));

        Label initial = new Label(
                user.getName().isEmpty() ? "?" : user.getName().substring(0, 1).toUpperCase());
        initial.setTextFill(Color.WHITE);
        initial.setFont(Font.font("Arial", FontWeight.BOLD, 32));

        photoPane.getChildren().addAll(circle, initial);

        VBox userInfoBox = new VBox(12);
        userInfoBox.setAlignment(Pos.CENTER_LEFT);
        userInfoBox.setPadding(new Insets(10, 0, 0, 0));

        VBox nameLabel = createInfoLabel("Name", user.getName());
        VBox emailLabel = createInfoLabel("E-mail", user.getEmail());
        VBox registeredLabel = createInfoLabel("Created at",
                user.getRegisteredAt().format(DATE_FORMATTER));

        userInfoBox.getChildren().addAll(nameLabel, emailLabel, registeredLabel);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> close());
        closeButton.setDefaultButton(true);
        closeButton.getStyleClass().add("um-id-close-button");

        root.getChildren().addAll(photoPane, userInfoBox, closeButton);

        Scene scene = new Scene(root);
        uiController.loadStylesheetToScene(scene, CSS.USERS_MANAGEMENT);
        setScene(scene);
    }

    /**
     * Creates a labeled VBox for displaying user information.
     *
     * @param title   The title of the information (e.g., "Name", "E-mail").
     * @param content The content to display (e.g., user's name, email).
     * @return A VBox containing the title and content labels.
     */
    private VBox createInfoLabel(String title, String content) {
        Label titleLabel = new Label(title + ":");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Arial", 13));
        contentLabel.setWrapText(true);

        return new VBox(2, titleLabel, contentLabel);
    }

}

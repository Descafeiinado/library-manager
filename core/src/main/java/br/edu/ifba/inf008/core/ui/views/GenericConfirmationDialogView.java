package br.edu.ifba.inf008.core.ui.views;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.ui.CSS;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GenericConfirmationDialogView extends Stage {

    private static final IUIController uiController = ICore.getInstance().getUIController();

    private Runnable onConfirmedClick;

    public GenericConfirmationDialogView(String title, String message) {
        setTitle(title);

        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);

        VBox formLayout = new VBox(15);
        formLayout.setPadding(new Insets(20));
        formLayout.setPrefWidth(350);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("confirmation-form-title");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("confirmation-message-label");

        Button cancelButton = new Button("Cancel");
        Button confirmButton = new Button("Confirm");

        HBox buttonRow = new HBox(10, cancelButton, confirmButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        cancelButton.getStyleClass().add("confirmation-cancel-button");
        confirmButton.getStyleClass().add("confirmation-confirm-button");

        cancelButton.setOnAction(e -> close());

        confirmButton.setOnAction(e -> {
            if (onConfirmedClick != null) {
                onConfirmedClick.run();
            }
            close();
        });

        formLayout.getChildren().addAll(titleLabel, messageLabel, buttonRow);

        Scene scene = new Scene(formLayout);
        uiController.loadStylesheetToScene(scene, CSS.GENERIC_CONFIRMATION_DIALOG);
        setScene(scene);
    }

    public void setOnConfirmedClick(Runnable onConfirmedClick) {
        this.onConfirmedClick = onConfirmedClick;
    }

}

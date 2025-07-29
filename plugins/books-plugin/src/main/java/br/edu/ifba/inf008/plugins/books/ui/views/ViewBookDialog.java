package br.edu.ifba.inf008.plugins.books.ui.views;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.infrastructure.managers.BookAvailabilityManager;
import br.edu.ifba.inf008.plugins.books.ui.CSS;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ViewBookDialog extends Stage {

    private static final BookAvailabilityManager bookAvailabilityManager = BookAvailabilityManager.getInstance();
    private static final IUIController uiController = ICore.getInstance().getUIController();

    public ViewBookDialog(Book book) {
        setTitle("Book Details");

        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(320);
        root.getStyleClass().add("bm-id-card");

        StackPane photoPane = new StackPane();
        photoPane.setPrefSize(80, 80);

        Circle circle = new Circle(40);
        circle.setFill(Color.web("#0066cc"));

        String initialChar = (book.getTitle() != null && !book.getTitle().isEmpty())
                ? book.getTitle().substring(0, 1).toUpperCase()
                : "?";

        Label initial = new Label(initialChar);
        initial.setTextFill(Color.WHITE);
        initial.setFont(Font.font("Arial", FontWeight.BOLD, 32));

        photoPane.getChildren().addAll(circle, initial);

        VBox bookInfoBox = new VBox(12);
        bookInfoBox.setAlignment(Pos.CENTER_LEFT);
        bookInfoBox.setPadding(new Insets(10, 0, 0, 0));

        String titleWithYear = book.getTitle();
        if (book.getPublishedYear() != null) {
            titleWithYear += " (" + book.getPublishedYear() + ")";
        }

        String copiesInformation =
                bookAvailabilityManager.getAvailableCopies(book.getBookId()) + " of "
                        + book.getCopiesAvailable() + " total";

        bookInfoBox.getChildren().addAll(
                createInlineLabel("#", String.valueOf(book.getBookId())),
                createInfoLabel("Title", titleWithYear),
                createInfoLabel("Author", book.getAuthor()),
                createInfoLabel("ISBN", book.getIsbn()),
                createInlineLabel("Copies Available", copiesInformation)
        );

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> close());
        closeButton.setDefaultButton(true);
        closeButton.getStyleClass().add("bm-id-close-button");

        root.getChildren().addAll(photoPane, bookInfoBox, closeButton);

        Scene scene = new Scene(root);
        uiController.loadStylesheetToScene(scene, CSS.BOOKS_MANAGEMENT);
        setScene(scene);
    }

    private VBox createInfoLabel(String title, String content) {
        Label titleLabel = new Label(title + ":");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Label contentLabel = new Label(content != null ? content : "(not provided)");
        contentLabel.setFont(Font.font("Arial", 13));
        contentLabel.setWrapText(true);

        return new VBox(2, titleLabel, contentLabel);
    }

    private HBox createInlineLabel(String title, String content) {
        Label titleLabel = new Label(title + ":");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Label contentLabel = new Label(content != null ? content : "(not provided)");
        contentLabel.setFont(Font.font("Arial", 13));

        HBox box = new HBox(titleLabel, contentLabel);
        box.setSpacing(4);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

}

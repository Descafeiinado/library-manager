package br.edu.ifba.inf008.plugins.loans.application.extensions.reports.views;

import static br.edu.ifba.inf008.core.ui.CSS.TABLE_OF_CONTENTS;
import static br.edu.ifba.inf008.plugins.loans.ui.CSS.LOAN_MANAGEMENT;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.ui.components.table.TableComponent;
import br.edu.ifba.inf008.plugins.loans.application.extensions.reports.models.LoanedBookInformationModel;
import br.edu.ifba.inf008.plugins.loans.domain.entities.Loan;
import br.edu.ifba.inf008.plugins.loans.infrastructure.repositories.LoanRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
 * Dialog for viewing all ongoing loans by a specific book. This dialog displays a circular photo
 * placeholder for the book, its title, availability information, and a table listing all ongoing
 * loans for that book.
 */
public class LoansByBookDialog extends Stage {

    private static final IUIController uiController = ICore.getInstance().getUIController();
    private static final LoanRepository loanRepository = LoanRepository.getInstance();

    public LoansByBookDialog(LoanedBookInformationModel information) {
        setTitle("Report: Loans by Book");

        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);

        setResizable(false);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("lm-id-card");

        root.setPrefWidth(790);
        root.setMaxHeight(728);
        root.setPrefHeight(728);
        root.setMinHeight(728);

        StackPane photoPane = new StackPane();
        photoPane.setPrefSize(80, 80);

        Circle circle = new Circle(40);
        circle.setFill(Color.web("#0066cc"));

        Label initial = new Label(
                information.getBookTitle().isEmpty() ? "?"
                        : information.getBookTitle().substring(0, 1).toUpperCase());
        initial.setTextFill(Color.WHITE);
        initial.setFont(Font.font("Arial", FontWeight.BOLD, 32));

        photoPane.getChildren().addAll(circle, initial);

        VBox reportInfoBox = new VBox(12);
        reportInfoBox.setAlignment(Pos.CENTER_LEFT);
        reportInfoBox.setPadding(new Insets(10, 0, 0, 0));

        VBox titleLabel = createInfoLabel("Title", information.getBookTitle());
        VBox availabilityLabel = createInfoLabel("Availability",
                information.getAvailableCopies() + " of " + information.getTotalCopies()
                        + " copies available");

        reportInfoBox.getChildren().addAll(titleLabel, availabilityLabel);

        TableComponent<Loan> loansByBookTable = new TableComponent<>(
                Loan.class,
                (page, size) -> loanRepository.findAllByBookIdAndNotReturned(
                        information.getBookId(), page, size),
                List.of("loanDate")
        );

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> close());
        closeButton.setDefaultButton(true);
        closeButton.getStyleClass().add("lm-id-close-button");

        root.getChildren().addAll(photoPane, reportInfoBox, loansByBookTable, closeButton);

        Scene scene = new Scene(root);
        uiController.loadStylesheetToScene(scene, TABLE_OF_CONTENTS);
        uiController.loadStylesheetToScene(scene, LOAN_MANAGEMENT);
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

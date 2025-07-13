package br.edu.ifba.inf008.infrastructure.controllers;

import br.edu.ifba.inf008.Core;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.ICore;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class UIController extends Application implements IUIController {
    private ICore core;
    private MenuBar menuBar;
    private static UIController uiController;

    private VBox sidebar;
    private BorderPane rootLayout;
    private Map<String, Node> tabContents;
    private VBox centerContent;

    private static final String MAIN_TAB = "Início";

    public UIController() {}

    @Override
    public void init() {
        uiController = this;
    }

    public static UIController getInstance() {
        return uiController;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Federal Institute Library Manager");

        tabContents = new HashMap<>();

        menuBar = new MenuBar();

        sidebar = new VBox();
        sidebar.setPadding(new Insets(10));
        sidebar.setSpacing(10);
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #f0f0f0;");

        centerContent = new VBox();
        centerContent.setPadding(new Insets(20));
        centerContent.setAlignment(Pos.TOP_LEFT);
        centerContent.setSpacing(10);

        rootLayout = new BorderPane();
        rootLayout.setTop(menuBar);
        rootLayout.setLeft(sidebar);
        rootLayout.setCenter(centerContent);

        Scene scene = new Scene(rootLayout, 960, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        createMainTab();

        Core.getInstance().getPluginController().init();

        if (Core.getInstance().getPluginController().getEnabledPlugins().isEmpty()) {
            displayNullAlert();
        }
    }

    private void createMainTab() {
        Button mainButton = new Button(MAIN_TAB);
        mainButton.setMaxWidth(Double.MAX_VALUE);
        mainButton.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER_LEFT;");
        mainButton.setOnAction(e -> centerContent.getChildren().setAll(new Label("Bem-vindo ao sistema da biblioteca!")));

        sidebar.getChildren().add(mainButton);
        centerContent.getChildren().setAll(new Label("Bem-vindo ao sistema da biblioteca!"));
    }

    public boolean createTab(String tabText, Node contents) {
        if (contents == null) return false;

        Button tabButton = new Button(tabText);
        tabButton.setMaxWidth(Double.MAX_VALUE);
        tabButton.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER_LEFT;");
        tabButton.setOnAction(e -> updateCenterContent(tabText));

        sidebar.getChildren().add(tabButton);
        tabContents.put(tabText, contents);

        return true;
    }

    private void updateCenterContent(String tabText) {
        centerContent.getChildren().clear();

        Node content = tabContents.get(tabText);

        if (content != null) {
            centerContent.getChildren().add(content);
        } else displayNullAlert();
    }

    private void displayNullAlert() {
        centerContent.getChildren().clear();

        Label warning = new Label("Erro: Nenhum plugin foi carregado, então não há conteúdo para exibir.");

        warning.setTextFill(Color.RED);
        warning.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        centerContent.getChildren().add(warning);
    }
}

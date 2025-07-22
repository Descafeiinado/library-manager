package br.edu.ifba.inf008.infrastructure.controllers;

import br.edu.ifba.inf008.Core;
import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.ui.models.TabInformation;
import br.edu.ifba.inf008.ui.Icons;
import br.edu.ifba.inf008.ui.components.SidebarPane;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class UIController extends Application implements IUIController {

    private static final String MAIN_TAB = "Homepage";
    private static UIController uiController = new UIController();

    private Map<String, Node> tabContents;

    private SidebarPane sidebar;
    private VBox centerContent;

    public UIController() {
    }

    @Override
    public void init() {
        uiController = this;
    }

    @Override
    public void postPluginInit() {
        if (Core.getInstance().getPluginController().getEnabledPlugins().isEmpty()) {
            displayNullAlert();
        }
    }

    @Override
    public ImageView loadIcon(String path) {
        Image image = new Image(getClass().getResourceAsStream(path), 0, 0, true, true); // load full quality

        ImageView icon = new ImageView(image);
        icon.setFitWidth(16);
        icon.setFitHeight(16);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);

        return icon;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Federal Institute Library Manager");
        primaryStage.setResizable(false);
        primaryStage.setMaximized(false);

        tabContents = new HashMap<>();

        MenuBar menuBar = new MenuBar();

        sidebar = new SidebarPane(this::updateCenterContent);

        centerContent = new VBox();
        centerContent.setPadding(new Insets(20));
        centerContent.setAlignment(Pos.TOP_LEFT);
        centerContent.setSpacing(10);

        BorderPane rootLayout = new BorderPane();

        rootLayout.setTop(menuBar);
        rootLayout.setLeft(sidebar);
        rootLayout.setCenter(centerContent);

        Scene scene = new Scene(rootLayout, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();

        scene.getStylesheets().add(getClass().getResource("/css/ui.css").toExternalForm());

        createMainTab();

        ICore core = Core.getInstance();

        try {
            core.startup();
        } catch (Exception e) {
            core.shutdown();

            System.out.println("Failed to init system: " + e.getMessage());
            e.printStackTrace();

            System.exit(-1);
        }
    }

    @Override
    public boolean createTab(TabInformation tabInformation, Node contents) {
        if (contents == null) {
            return false;
        }

        sidebar.addTab(tabInformation.text(), tabInformation.icon());
        tabContents.put(tabInformation.text(), contents);

        return true;
    }

    private void createMainTab() {
        sidebar.addTab(MAIN_TAB, loadIcon(Icons.HOUSE));
        sidebar.selectTab(MAIN_TAB);

        Label title = new Label("Welcome to the library system!");
        title.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;" +
                        "-fx-padding: 5px; -fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;"
        );

        Label description = new Label(
                "This is the main tab of the library system. You can access various features from here.");
        description.setStyle(
                "-fx-font-size: 16px; -fx-text-fill: #666666; -fx-padding: 5px; " +
                        "-fx-border-radius: 5px; -fx-background-radius: 5px;"
        );

        VBox mainTabContent = new VBox();
        mainTabContent.getChildren().addAll(title, description);

        centerContent.getChildren().add(mainTabContent);
        tabContents.put(MAIN_TAB, mainTabContent);
    }

    private void updateCenterContent(String tabText) {
        centerContent.getChildren().clear();

        Node content = tabContents.get(tabText);

        if (content != null) {
            centerContent.getChildren().add(content);
        } else {
            displayNullAlert();
        }
    }

    private void displayNullAlert() {
        centerContent.getChildren().clear();

        Label warning = new Label(
                "Error: No plugins enabled. Please enable at least one plugin to use the system.");

        warning.setTextFill(Color.RED);
        warning.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        centerContent.getChildren().add(warning);
    }

    public static UIController getInstance() {
        return uiController;
    }
}

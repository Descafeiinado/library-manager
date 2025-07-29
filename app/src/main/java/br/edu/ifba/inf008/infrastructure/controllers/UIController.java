package br.edu.ifba.inf008.infrastructure.controllers;

import br.edu.ifba.inf008.Core;
import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.ui.CSS;
import br.edu.ifba.inf008.core.ui.Icons;
import br.edu.ifba.inf008.core.ui.components.SidebarComponent;
import br.edu.ifba.inf008.core.ui.models.TabInformation;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
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

    private final Map<String, Supplier<Node>> lazyTabContents = new HashMap<>();

    private SidebarComponent sidebar;
    private VBox centerContent;
    private Scene scene;

    public UIController() {
    }

    public static UIController getInstance() {
        return uiController;
    }

    @Override
    public void init() {
        uiController = this;
    }

    @Override
    public Scene getMainScene() {
        if (scene == null) {
            throw new IllegalStateException(
                    "Main scene is not initialized yet. Call start() first.");
        }

        return scene;
    }

    @Override
    public void postPluginInit() {
        if (Core.getInstance().getPluginController().getEnabledPlugins().isEmpty()) {
            displayNullAlert();
        }
    }

    @Override
    public void loadStylesheetToScene(Scene scene, String path) {
        if (tryLoadStylesheet(scene, path, getClass().getClassLoader())) {
            return;
        }

        System.err.println(
                "Failed to load stylesheet: " + path + " - Searching in plugin class loaders.");

        boolean loadedFromPlugins = false;

        for (ClassLoader pluginClassLoader : Core.getInstance().getPluginController()
                .getPluginClassLoaders()) {
            if (tryLoadStylesheet(scene, path, pluginClassLoader)) {
                loadedFromPlugins = true;
                break;
            }
        }

        if (!loadedFromPlugins) {
            System.err.println(
                    "Even plugin class loaders couldn't resolve the stylesheet: " + path);
        }
    }

    private boolean tryLoadStylesheet(Scene scene, String path, ClassLoader loader) {
        String correctedPath = correctClassLoaderPath(path);

        URL stylesheetResource = loader.getResource(correctedPath);
        String loaderName = loader.getClass().getSimpleName();

        if (stylesheetResource == null) {
            return false;
        }

        try {
            scene.getStylesheets().add(stylesheetResource.toExternalForm());
            System.out.println("[" + loaderName + "] Loaded stylesheet: " + correctedPath);
            return true;
        } catch (Exception e) {
            System.err.println(
                    "[" + loaderName + "] Failed to load stylesheet: " + correctedPath + " - "
                            + e.getMessage());
            return false;
        }
    }

    @Override
    public ImageView loadIcon(String path) {
        ImageView fallbackIcon = createIconFromPath(Icons.HOUSE, getClass().getClassLoader());

        if (fallbackIcon == null) {
            throw new RuntimeException("Fallback icon not found: " + Icons.HOUSE);
        }

        ImageView icon = createIconFromPath(path, getClass().getClassLoader());

        if (icon != null) {
            return icon;
        }

        for (ClassLoader pluginClassLoader : Core.getInstance().getPluginController()
                .getPluginClassLoaders()) {
            icon = createIconFromPath(path, pluginClassLoader);

            if (icon != null) {
                System.out.println(
                        "Loaded icon from plugin classloader: " + pluginClassLoader.getClass()
                                .getSimpleName());
                return icon;
            }
        }

        System.err.println("Failed to load icon from all classloaders: " + path);

        return fallbackIcon;
    }

    private ImageView createIconFromPath(String path, ClassLoader loader) {
        String correctedPath = correctClassLoaderPath(path);

        try (InputStream iconStream = loader.getResourceAsStream(correctedPath)) {
            if (iconStream == null) {
                return null;
            }

            Image image = new Image(iconStream, 0, 0, true, true);
            ImageView view = new ImageView(image);
            view.setFitWidth(16);
            view.setFitHeight(16);
            view.setPreserveRatio(true);
            view.setSmooth(true);

            System.out.println(
                    "[" + loader.getClass().getSimpleName() + "] Loaded icon: " + correctedPath);
            return view;
        } catch (Exception e) {
            System.err.println("[" + loader.getClass().getSimpleName() + "] Error loading icon: "
                    + correctedPath + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Federal Institute Library Manager");
        primaryStage.setResizable(false);
        primaryStage.setMaximized(false);

        MenuBar menuBar = new MenuBar();

        sidebar = new SidebarComponent(this::updateCenterContent);

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

        this.scene = scene;

        loadStylesheetToScene(this.scene, CSS.SIDEBAR);
        loadStylesheetToScene(this.scene, CSS.TABLE_OF_CONTENTS);

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
    public boolean createTab(TabInformation tabInformation, Supplier<Node> contentSupplier) {
        if (contentSupplier == null) {
            return false;
        }

        sidebar.addTab(tabInformation.text(), tabInformation.icon());
        lazyTabContents.put(tabInformation.text(), contentSupplier);

        return true;
    }

    private void createMainTab() {
        sidebar.addTab(MAIN_TAB, loadIcon(Icons.HOUSE));
        sidebar.selectTab(MAIN_TAB);

        Label title = new Label("Welcome to the library system!");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;"
                + "-fx-padding: 5px; -fx-border-radius: 5px;" + "-fx-background-radius: 5px;");

        Label description = new Label(
                "This is the main tab of the library system. You can access various features from here.");
        description.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666; -fx-padding: 5px; "
                + "-fx-border-radius: 5px; -fx-background-radius: 5px;");

        VBox mainTabContent = new VBox();
        mainTabContent.getChildren().addAll(title, description);

        centerContent.getChildren().add(mainTabContent);
        lazyTabContents.put(MAIN_TAB, () -> mainTabContent);
    }

    private void updateCenterContent(String tabText) {
        centerContent.getChildren().clear();

        Supplier<Node> supplier = lazyTabContents.get(tabText);

        if (supplier != null) {
            Node content = supplier.get();
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

    private String correctClassLoaderPath(String original) {
        return original.startsWith("/") ? original.substring(1) : original;
    }

}

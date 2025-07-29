package br.edu.ifba.inf008.core.ui.components;

import br.edu.ifba.inf008.core.ui.SFX;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * SidebarComponent is a UI component that represents a sidebar with tab buttons. It allows adding
 * tabs with optional icons and handles tab selection. When a tab is selected, it plays a click
 * sound and notifies the provided callback.
 */
public class SidebarComponent extends VBox {

    /**
     * Map to hold the tab buttons with their names as keys.
     */
    private final Map<String, Button> tabButtons = new HashMap<>();

    /**
     * Callback to be executed when a tab is selected. It receives the name of the selected tab as
     * an argument.
     */
    private final Consumer<String> onTabSelected;

    /**
     * The currently selected tab name.
     */
    private String selectedTab = null;

    public SidebarComponent(Consumer<String> onTabSelected) {
        this.onTabSelected = onTabSelected;

        this.setPadding(new Insets(10));
        this.setSpacing(4);
        this.setPrefWidth(200);
        this.getStyleClass().add("sidebar");
    }

    /**
     * Adds a new tab to the sidebar with the specified name. The tab will not have an icon.
     *
     * @param name The name of the tab to be added.
     */
    public void addTab(String name) {
        addTab(name, null);
    }

    /**
     * Adds a new tab to the sidebar with the specified name and icon.
     *
     * @param name The name of the tab to be added.
     * @param icon The icon to be displayed on the tab, can be null if no icon is needed.
     */
    public void addTab(String name, Node icon) {
        Button button = new Button(name);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("sidebar-button");

        if (icon != null) {
            button.setGraphic(icon);
        }

        button.setOnAction(e -> {
            if (selectedTab != null && selectedTab.equals(name)) {
                return;
            }

            playClickSound();

            selectTab(name);
            onTabSelected.accept(name);
        });

        this.getChildren().add(button);
        tabButtons.put(name, button);
    }

    /**
     * Removes a tab from the sidebar by its name.
     *
     * @param name The name of the tab to be removed.
     */
    public void selectTab(String name) {
        if (selectedTab != null) {
            tabButtons.get(selectedTab).getStyleClass().remove("selected");
        }

        selectedTab = name;
        if (tabButtons.containsKey(name)) {
            tabButtons.get(name).getStyleClass().add("selected");
        }
    }

    /**
     * Gets the currently selected tab name.
     *
     * @return The name of the currently selected tab, or null if no tab is selected.
     */
    private void playClickSound() {
        try {
            URL soundURL = getClass().getResource(SFX.CLICK);

            if (soundURL != null) {
                Media media = new Media(soundURL.toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);

                mediaPlayer.play();
            }
        } catch (Exception ignored) {
        }
    }

}

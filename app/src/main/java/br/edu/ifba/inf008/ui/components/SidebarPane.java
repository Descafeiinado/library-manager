package br.edu.ifba.inf008.ui.components;

import java.net.URL;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.scene.media.MediaPlayer;

public class SidebarPane extends VBox {

    private final Map<String, Button> tabButtons = new HashMap<>();
    private String selectedTab = null;
    private final Consumer<String> onTabSelected;

    public SidebarPane(Consumer<String> onTabSelected) {
        this.onTabSelected = onTabSelected;

        this.setPadding(new Insets(10));
        this.setSpacing(4);
        this.setPrefWidth(200);
        this.getStyleClass().add("sidebar");
    }

    public void addTab(String name) {
        addTab(name, null);
    }

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

    public void selectTab(String name) {
        if (selectedTab != null) {
            tabButtons.get(selectedTab).getStyleClass().remove("selected");
        }

        selectedTab = name;
        if (tabButtons.containsKey(name)) {
            tabButtons.get(name).getStyleClass().add("selected");
        }
    }

    private void playClickSound() {
        try {
            URL soundURL = getClass().getResource("/sfx/click.wav");

            if (soundURL != null) {
                Media media = new Media(soundURL.toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);

                mediaPlayer.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

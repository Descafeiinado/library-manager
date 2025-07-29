package br.edu.ifba.inf008.core;

import br.edu.ifba.inf008.core.ui.models.TabInformation;
import java.util.function.Supplier;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;

public interface IUIController {

    void postPluginInit();

    boolean createTab(TabInformation tabInformation, Supplier<Node> contents);

    ImageView loadIcon(String path);

    void loadStylesheetToScene(Scene scene, String path);

    Scene getMainScene();
}

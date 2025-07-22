package br.edu.ifba.inf008.core;

import br.edu.ifba.inf008.core.ui.models.TabInformation;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public interface IUIController {

    void postPluginInit();

    boolean createTab(TabInformation tabInformation, Node contents);

    ImageView loadIcon(String path);
}

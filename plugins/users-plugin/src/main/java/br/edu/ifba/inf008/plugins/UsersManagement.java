package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.core.IPlugin;
import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IUIController;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class UsersManagement implements IPlugin
{
    public boolean init() {
        IUIController uiController = ICore.getInstance().getUIController();

        uiController.createTab("new tab", new Rectangle(200,200, Color.LIGHTSTEELBLUE));

        return true;
    }
}

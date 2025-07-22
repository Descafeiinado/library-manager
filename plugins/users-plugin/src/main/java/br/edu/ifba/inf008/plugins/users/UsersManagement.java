package br.edu.ifba.inf008.plugins.users;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IPlugin;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.domain.annotations.Plugin;
import br.edu.ifba.inf008.core.infrastructure.managers.HibernateManager;
import br.edu.ifba.inf008.core.ui.models.TabInformation;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.ui.Icons;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

@Plugin(name = "users-management")
public class UsersManagement implements IPlugin {

    public boolean init() {
        ICore core = ICore.getInstance();
        IUIController uiController = core.getUIController();

        uiController.createTab(new TabInformation("Users", uiController.loadIcon(Icons.USERS)),
                new Rectangle(200, 200, Color.LIGHTSTEELBLUE));

        HibernateManager.registerEntityClass(User.class);

        return true;
    }
}

package br.edu.ifba.inf008.plugins.users;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IPlugin;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.domain.annotations.Plugin;
import br.edu.ifba.inf008.core.infrastructure.managers.HibernateManager;
import br.edu.ifba.inf008.core.ui.models.TabInformation;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.application.services.UserService;
import br.edu.ifba.inf008.plugins.users.ui.CSS;
import br.edu.ifba.inf008.plugins.users.ui.PluginIcons;
import br.edu.ifba.inf008.plugins.users.ui.views.MainView;

@Plugin(name = "users-management")
public class UsersManagement implements IPlugin {

    private IUIController uiController;
    private UserService userService;

    @Override
    public boolean init() {
        ICore core = ICore.getInstance();

        HibernateManager.registerEntityClass(User.class);

        userService = UserService.getInstance();

        uiController = core.getUIController();
        uiController.loadStylesheetToScene(uiController.getMainScene(), CSS.USERS_MANAGEMENT);

        uiController.createTab(
                new TabInformation("Users", uiController.loadIcon(PluginIcons.USERS)),
                MainView.supply(uiController, userService));

        return true;
    }

}

package br.edu.ifba.inf008;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IPluginController;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.infrastructure.managers.HibernateManager;
import br.edu.ifba.inf008.infrastructure.controllers.PluginController;
import br.edu.ifba.inf008.infrastructure.controllers.UIController;

public class Core extends ICore {

    private final IPluginController pluginController = new PluginController();

    private Core() {
    }

    public static void init() {
        System.out.println("Initializing core...");

        if (instance != null) {
            System.out.println("Fatal error: core is already initialized!");
            System.exit(-1);
        }

        instance = new Core();

        UIController.launch(UIController.class);

        Core.getInstance().shutdown();
    }

    @Override
    public void startup() {
        Core.getInstance().getPluginController().init();
        Core.getInstance().getUIController().postPluginInit();

        HibernateManager.buildSessionFactory();
    }

    @Override
    public void shutdown() {
        HibernateManager.shutdownSessionFactory();
    }

    public IUIController getUIController() {
        return UIController.getInstance();
    }

    public IPluginController getPluginController() {
        return pluginController;
    }

}

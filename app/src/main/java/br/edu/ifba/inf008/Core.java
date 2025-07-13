package br.edu.ifba.inf008;

import br.edu.ifba.inf008.core.*;
import br.edu.ifba.inf008.infrastructure.controllers.PluginController;
import br.edu.ifba.inf008.infrastructure.controllers.UIController;

public class Core extends ICore {
  private Core() {
  }

  public static boolean init() {
    if (instance != null) {
      System.out.println("Fatal error: core is already initialized!");
      System.exit(-1);
    }

    instance = new Core();
    UIController.launch(UIController.class);

    return true;
  }

  public IUIController getUIController() {
    return UIController.getInstance();
  }

  public IPluginController getPluginController() {
    return pluginController;
  }

  private IPluginController pluginController = new PluginController();
}

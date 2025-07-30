package br.edu.ifba.inf008.plugins.reports;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IPlugin;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.domain.annotations.Plugin;
import br.edu.ifba.inf008.core.ui.models.TabInformation;
import br.edu.ifba.inf008.plugins.reports.ui.CSS;
import br.edu.ifba.inf008.plugins.reports.ui.PluginIcons;
import br.edu.ifba.inf008.plugins.reports.ui.views.MainView;

@Plugin(name = "reports")
public class Reports implements IPlugin {

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void postInit() {
        ICore core = ICore.getInstance();
        IUIController uiController = core.getUIController();

        uiController.loadStylesheetToScene(uiController.getMainScene(), CSS.REPORTS);

        uiController.createTab(
                new TabInformation("Reports", uiController.loadIcon(PluginIcons.REPORTS)),
                MainView.supply(uiController));
    }

}

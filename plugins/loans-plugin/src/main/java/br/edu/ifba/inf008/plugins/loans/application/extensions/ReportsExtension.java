package br.edu.ifba.inf008.plugins.loans.application.extensions;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IPluginController;
import java.lang.reflect.Method;
import java.util.List;

public class ReportsExtension {

    private static String REPORTS_PLUGIN_ID = "reports";
    private static List<String> REPORTS = List.of(
            "br.edu.ifba.inf008.plugins.loans.application.extensions.reports.LoanedBooksReport"
    );

    public static void initialize() {
        ICore core = ICore.getInstance();
        IPluginController pluginController = core.getPluginController();

        if (!pluginController.isPluginEnabled(REPORTS_PLUGIN_ID)) {
            return;
        }

        try {
            Class<?> reportsRepositoryClass = Class.forName(
                    "br.edu.ifba.inf008.plugins.reports.infrastructure.repositories.ReportsRepository");
            Method getInstanceMethod = reportsRepositoryClass.getMethod("getInstance");

            Object reportsRepositoryInstance = getInstanceMethod.invoke(null);

            Method saveReportMethod = reportsRepositoryClass.getMethod("save", Object.class);

            for (String reportClassName : REPORTS) {
                Class<?> reportClass = Class.forName(reportClassName);
                Object reportInstance = reportClass.getDeclaredConstructor().newInstance();

                saveReportMethod.invoke(reportsRepositoryInstance, reportInstance);
            }

        } catch (Exception e) {
            throw new RuntimeException("Reports plugin is not installed or not available.", e);
        }
    }

}

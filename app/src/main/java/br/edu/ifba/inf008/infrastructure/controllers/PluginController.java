package br.edu.ifba.inf008.infrastructure.controllers;

import br.edu.ifba.inf008.App;
import br.edu.ifba.inf008.core.IPluginController;
import br.edu.ifba.inf008.core.IPlugin;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class PluginController implements IPluginController {
  private final List<String> enabledPlugins = new ArrayList<>();

  public List<String> getEnabledPlugins() {
    return enabledPlugins;
  }

  public boolean isPluginEnabled(String pluginName) {
    return enabledPlugins.contains(pluginName);
  }

  public void init() {
    try {
      File currentDir = new File("./plugins");
      FilenameFilter jarFilter = (_, name) -> name.toLowerCase().endsWith(".jar");

      String[] pluginFiles = currentDir.list(jarFilter);

      if (pluginFiles == null || pluginFiles.length == 0) {
        System.out.println("No plugins found in the ./plugins directory.");
        return;
      }

      URL[] jarFilesURL = new URL[pluginFiles.length];

      for (int index = 0; index < pluginFiles.length; index++) {
        jarFilesURL[index] = (new File("./plugins/" + pluginFiles[index])).toURI().toURL();
      }

      URLClassLoader urlClassLoader = new URLClassLoader(jarFilesURL, App.class.getClassLoader());

      for (String pluginFile : pluginFiles) {
        String pluginName = pluginFile.split("\\.")[0];
        Class<?> pluginClass = Class.forName("br.edu.ifba.inf008.plugins." + pluginName, true, urlClassLoader);

        IPlugin plugin = (IPlugin) pluginClass.getDeclaredConstructor().newInstance();

        try {
          plugin.init();
        } catch (Exception e) {
          System.out.println("Failed to initialize plugin: " + pluginName + " - " + e.getMessage());
          continue;
        }

        enabledPlugins.add(pluginName);
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.getClass().getName() + " - " + e.getMessage());
    }
  }
}

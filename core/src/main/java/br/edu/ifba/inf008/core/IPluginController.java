package br.edu.ifba.inf008.core;

import java.util.LinkedHashSet;
import java.util.List;

public interface IPluginController {

    boolean isPluginEnabled(String pluginName);

    LinkedHashSet<ClassLoader> getPluginClassLoaders();
    List<String> getEnabledPlugins();

    void init();

}

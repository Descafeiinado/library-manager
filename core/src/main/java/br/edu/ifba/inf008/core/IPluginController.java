package br.edu.ifba.inf008.core;

import java.util.List;

public interface IPluginController
{
    List<String> getEnabledPlugins();

    boolean isPluginEnabled(String pluginName);
    void init();
}

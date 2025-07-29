package br.edu.ifba.inf008.core;

public abstract class ICore {

    protected static ICore instance = null;

    public static ICore getInstance() {
        return instance;
    }

    public abstract IUIController getUIController();

    public abstract IPluginController getPluginController();

    public abstract void shutdown();

    public abstract void startup();
}

package br.edu.ifba.inf008.infrastructure.controllers;

import br.edu.ifba.inf008.App;
import br.edu.ifba.inf008.core.IPlugin;
import br.edu.ifba.inf008.core.IPluginController;
import br.edu.ifba.inf008.core.domain.annotations.Plugin;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.jar.JarFile;

public class PluginController implements IPluginController {

    private final List<String> enabledPlugins = new ArrayList<>();
    private final LinkedHashSet<ClassLoader> pluginClassLoaders = new LinkedHashSet<>();

    public List<String> getEnabledPlugins() {
        return enabledPlugins;
    }

    public boolean isPluginEnabled(String pluginName) {
        return enabledPlugins.contains(pluginName);
    }

    public void init() {
        try {
            File pluginsDir = new File("./plugins");
            FilenameFilter jarFilter = (dir, name) -> name.toLowerCase().endsWith(".jar");

            String[] pluginFiles = pluginsDir.list(jarFilter);

            if (pluginFiles == null || pluginFiles.length == 0) {
                System.out.println("No plugins found in the ./plugins directory.");
                return;
            }

            for (String pluginFileName : pluginFiles) {
                File jarFile = new File(pluginsDir, pluginFileName);
                URL jarUrl = jarFile.toURI().toURL();
                URL[] urls = new URL[]{jarUrl};

                URLClassLoader urlClassLoader = new URLClassLoader(urls,
                        App.class.getClassLoader());

                try (JarFile jar = new JarFile(jarFile)) {
                    jar.stream().filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                            .forEach(jarEntry -> {
                                String className = jarEntry.getName().replace('/', '.')
                                        .replace(".class", "");

                                if (!className.startsWith("br.edu.ifba.inf008.plugins")) {
                                    return;
                                }

                                try {
                                    Class<?> clazz = urlClassLoader.loadClass(className);

                                    if (clazz.isAnnotationPresent(Plugin.class)) {
                                        if (IPlugin.class.isAssignableFrom(clazz)) {
                                            pluginClassLoaders.add(urlClassLoader);

                                            IPlugin pluginInstance = (IPlugin) clazz.getDeclaredConstructor()
                                                    .newInstance();

                                            Plugin annotation = clazz.getAnnotation(Plugin.class);

                                            pluginInstance.init();

                                            enabledPlugins.add(annotation.name());
                                            System.out.println(
                                                    "Plugin enabled: " + annotation.name());
                                        } else {
                                            System.out.println("Class " + className
                                                    + " is annotated with @Plugin but does not implement IPlugin interface.");
                                        }
                                    }
                                } catch (Exception e) {
                                    System.out.println(
                                            "Error loading plugin class " + className + ": "
                                                    + e.getMessage());
                                }
                            });
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }

    @Override
    public LinkedHashSet<ClassLoader> getPluginClassLoaders() {
        return pluginClassLoaders;
    }

}

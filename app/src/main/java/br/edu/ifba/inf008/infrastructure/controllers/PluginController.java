package br.edu.ifba.inf008.infrastructure.controllers;

import br.edu.ifba.inf008.App;
import br.edu.ifba.inf008.core.IPlugin;
import br.edu.ifba.inf008.core.IPluginController;
import br.edu.ifba.inf008.core.domain.annotations.Plugin;
import br.edu.ifba.inf008.infrastructure.models.PluginMetadata;
import br.edu.ifba.inf008.infrastructure.utils.CombinedPluginClassLoader;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * Controller responsible for discovering, resolving dependencies, and loading plugins dynamically
 * from the {@code ./plugins} directory.
 */
public class PluginController implements IPluginController {

    private static final File PLUGIN_DIRECTORY = new File("./plugins");

    private final List<String> enabledPlugins = new ArrayList<>();
    private final LinkedHashSet<ClassLoader> pluginClassLoaders = new LinkedHashSet<>();

    /**
     * Returns a list of names of successfully enabled plugins.
     *
     * @return a list of enabled plugin names.
     */
    @Override
    public List<String> getEnabledPlugins() {
        return enabledPlugins;
    }

    /**
     * Returns the set of class loaders used by the loaded plugins. The order is preserved according
     * to the load order.
     *
     * @return a set of plugin class loaders.
     */
    @Override
    public LinkedHashSet<ClassLoader> getPluginClassLoaders() {
        return pluginClassLoaders;
    }

    /**
     * Checks whether a plugin with the given name is enabled.
     *
     * @param pluginName the name of the plugin.
     * @return {@code true} if the plugin is enabled, {@code false} otherwise.
     */
    @Override
    public boolean isPluginEnabled(String pluginName) {
        return enabledPlugins.contains(pluginName);
    }

    /**
     * Initializes the plugin system by:
     * <ol>
     *     <li>Discovering available plugins in the {@code ./plugins} directory</li>
     *     <li>Resolving the correct load order based on declared dependencies</li>
     *     <li>Loading and initializing the plugins in the correct order</li>
     * </ol>
     */
    @Override
    public void init() {
        try {
            List<PluginMetadata> discoveredPlugins = discoverPlugins();
            List<PluginMetadata> loadOrder = resolveLoadOrder(discoveredPlugins);
            loadPlugins(loadOrder);
        } catch (Exception e) {
            System.err.println("Fatal plugin system error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Scans the plugin directory and discovers all classes annotated with {@link Plugin}.
     *
     * @return a list of metadata describing the discovered plugins.
     * @throws Exception if the scanning or class loading fails.
     */
    private List<PluginMetadata> discoverPlugins() throws Exception {
        String[] jarFiles = Optional.ofNullable(
                PLUGIN_DIRECTORY.list((dir, name) -> name.endsWith(".jar"))).orElse(new String[0]);

        if (jarFiles.length == 0) {
            System.out.println("No plugins found in the ./plugins directory.");
            return Collections.emptyList();
        }

        List<PluginMetadata> metadataList = new ArrayList<>();

        for (String fileName : jarFiles) {
            File jarFile = new File(PLUGIN_DIRECTORY, fileName);
            URL jarUrl = jarFile.toURI().toURL();
            URLClassLoader jarLoader = new URLClassLoader(new URL[]{jarUrl},
                    App.class.getClassLoader());

            try (JarFile jar = new JarFile(jarFile)) {
                jar.stream().filter(entry -> entry.getName().endsWith(".class"))
                        .map(entry -> entry.getName().replace('/', '.').replace(".class", ""))
                        .filter(name -> name.startsWith("br.edu.ifba.inf008.plugins")).forEach(
                                className -> inspectPluginClass(metadataList, jarLoader, className,
                                        jarUrl));
            }
        }

        return metadataList;
    }

    /**
     * Inspects a class to determine if it is a plugin, and adds it to the plugin metadata list if
     * applicable.
     *
     * @param plugins   the list to add the plugin metadata to.
     * @param loader    the class loader used to load the class.
     * @param className the fully-qualified name of the class.
     * @param jarUrl    the URL of the JAR file containing the class.
     */
    private void inspectPluginClass(List<PluginMetadata> plugins, URLClassLoader loader,
            String className, URL jarUrl) {
        try {
            Class<?> clazz = loader.loadClass(className);

            if (clazz.isAnnotationPresent(Plugin.class) && IPlugin.class.isAssignableFrom(clazz)) {
                Plugin annotation = clazz.getAnnotation(Plugin.class);
                plugins.add(new PluginMetadata(annotation.name(), annotation.dependencies(),
                        annotation.softDependencies(), className, loader, jarUrl));
            }
        } catch (Throwable e) {
            System.err.println("Error inspecting class " + className + ": " + e.getMessage());
        }
    }

    /**
     * Resolves the plugin load order based on hard dependencies. Plugins with unsatisfied
     * dependencies are skipped with a warning.
     *
     * @param plugins the list of discovered plugins.
     * @return a sorted list of plugins to load in the correct order.
     */
    private List<PluginMetadata> resolveLoadOrder(List<PluginMetadata> plugins) {
        plugins.sort(Comparator.comparing(PluginMetadata::name, String.CASE_INSENSITIVE_ORDER));
        List<PluginMetadata> loadOrder = new ArrayList<>();
        Set<String> loaded = new HashSet<>();
        List<PluginMetadata> pending = new ArrayList<>(plugins);

        boolean progress;

        do {
            progress = false;
            Iterator<PluginMetadata> iterator = pending.iterator();

            while (iterator.hasNext()) {
                PluginMetadata plugin = iterator.next();

                if (Arrays.stream(plugin.dependencies()).allMatch(loaded::contains)) {
                    loadOrder.add(plugin);
                    loaded.add(plugin.name());
                    iterator.remove();
                    progress = true;
                }
            }
        } while (progress);

        for (PluginMetadata plugin : pending) {
            System.err.printf("Plugin '%s' could not be loaded. Missing dependencies: %s%n",
                    plugin.name(), String.join(", ", Arrays.stream(plugin.dependencies())
                            .filter(dep -> !loaded.contains(dep)).toList()));
        }

        return loadOrder;
    }


    /**
     * Loads and initializes the plugins in the given load order using
     * {@link CombinedPluginClassLoader}.
     *
     * @param loadOrder the ordered list of plugins to load.
     */
    private void loadPlugins(List<PluginMetadata> loadOrder) {
        Map<String, ClassLoader> pluginLoaders = new HashMap<>();

        for (PluginMetadata plugin : loadOrder) {
            try {
                List<ClassLoader> parents = new ArrayList<>();
                parents.add(App.class.getClassLoader());

                for (String dependency : plugin.dependencies()) {
                    Optional.ofNullable(pluginLoaders.get(dependency)).ifPresent(parents::add);
                }

                for (String softDep : plugin.softDependencies()) {
                    Optional.ofNullable(pluginLoaders.get(softDep)).ifPresent(parents::add);
                }

                CombinedPluginClassLoader loader = new CombinedPluginClassLoader(
                        new URL[]{plugin.jarUrl()}, parents);

                Class<?> rawClass = loader.loadClass(plugin.className());
                Class<? extends IPlugin> pluginClass = rawClass.asSubclass(IPlugin.class);
                IPlugin instance = pluginClass.getDeclaredConstructor().newInstance();

                instance.init();
                enabledPlugins.add(plugin.name());
                pluginClassLoaders.add(loader);
                pluginLoaders.put(plugin.name(), loader);

                System.out.println("Plugin enabled: " + plugin.name());

                instance.postInit();
            } catch (Exception e) {
                System.err.println("Error loading plugin " + plugin.name() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

    }
}

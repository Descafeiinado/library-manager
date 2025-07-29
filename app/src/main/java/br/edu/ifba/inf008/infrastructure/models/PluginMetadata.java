package br.edu.ifba.inf008.infrastructure.models;

import br.edu.ifba.inf008.core.IPlugin;
import java.net.URL;
import java.net.URLClassLoader;

public record PluginMetadata(String name, String[] dependencies, String[] softDependencies,
                             String className, URLClassLoader initialClassLoader,
                             URL jarUrl) {

}

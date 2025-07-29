package br.edu.ifba.inf008.infrastructure.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * A class loader that combines multiple class loaders to load classes and resources. It first
 * attempts to load classes from its own URLs, then from the provided parent class loaders.
 */
public class CombinedPluginClassLoader extends URLClassLoader {

    private final List<ClassLoader> delegates;

    public CombinedPluginClassLoader(URL[] urls, List<ClassLoader> parents) {
        super(urls, null);
        this.delegates = new ArrayList<>(parents);
    }

    /**
     * Finds a class by its name.
     *
     * @param name the name of the class
     * @return the Class object for the specified class name
     * @throws ClassNotFoundException if the class cannot be found in this class loader or any of
     *                                the delegates
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException ignored) {
        }

        for (ClassLoader delegate : delegates) {
            try {
                return delegate.loadClass(name);
            } catch (ClassNotFoundException ignored) {
            }
        }

        throw new ClassNotFoundException(name);
    }

    /**
     * Finds a resource by its name.
     *
     * @param name the name of the resource
     * @return a URL to the resource, or null if the resource cannot be found
     */
    @Override
    public URL getResource(String name) {
        URL res = super.getResource(name);
        if (res != null) {
            return res;
        }

        for (ClassLoader delegate : delegates) {
            res = delegate.getResource(name);
            if (res != null) {
                return res;
            }
        }

        return null;
    }
}

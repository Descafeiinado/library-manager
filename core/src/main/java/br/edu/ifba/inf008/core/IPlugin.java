package br.edu.ifba.inf008.core;

public interface IPlugin {

    boolean init();

    default boolean postInit() {
        return true;
    }
}

package br.edu.ifba.inf008.core;

public interface IPlugin {

    boolean init();

    default void postInit() {
    }
}

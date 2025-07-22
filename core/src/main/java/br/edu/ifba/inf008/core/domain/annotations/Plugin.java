package br.edu.ifba.inf008.core.domain.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {

    /**
     * The name of the plugin.
     *
     * @return the name of the plugin
     */
    String name();

}

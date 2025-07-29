package br.edu.ifba.inf008.core.domain.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {

    /**
     * The name of the plugin.
     * @return the name of the plugin
     */
    String name();

    /**
     * Required dependencies for the plugin.
     * @return an array of dependency names
     */
    String[] dependencies() default {};

    /**
     * Optional soft dependencies for the plugin.
     * @return an array of soft dependency names
     */
    String[] softDependencies() default {};

}

package br.edu.ifba.inf008.core.ui.components.table.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to specify the label for a table.
 * This annotation can be used to define a descriptive label for a table,
 * which can be displayed in the user interface.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface TableLabel {

    String value();
}

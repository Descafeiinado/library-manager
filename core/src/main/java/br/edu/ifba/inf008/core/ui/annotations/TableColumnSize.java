package br.edu.ifba.inf008.core.ui.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to specify the size of a table column.
 * This annotation can be used to define the width of a column in a table.
 * The value is specified as a double, representing the size in pixels or any other unit.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumnSize {

    double value();
}


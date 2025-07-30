package br.edu.ifba.inf008.core.ui.components.table.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javafx.geometry.Pos;

/**
 * Annotation to specify the orientation of a table column. This annotation can be used to define
 * the alignment of the content within a column in a table.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumnOrientation {

    Pos value() default Pos.CENTER;
}

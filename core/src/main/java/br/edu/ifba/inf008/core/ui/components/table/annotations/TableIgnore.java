package br.edu.ifba.inf008.core.ui.components.table.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a field should be ignored in table representations. This annotation
 * can be used to exclude specific fields from being displayed in tables. It is typically applied to
 * fields in data models that are not relevant for table views.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableIgnore {
}

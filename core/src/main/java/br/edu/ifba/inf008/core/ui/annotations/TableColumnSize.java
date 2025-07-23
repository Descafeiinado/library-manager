package br.edu.ifba.inf008.core.ui.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumnSize {
    double value();
}


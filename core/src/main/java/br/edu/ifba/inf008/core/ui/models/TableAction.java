package br.edu.ifba.inf008.core.ui.models;

import java.util.function.Predicate;

public interface TableAction<T> {
    String getLabel();
    String getIconPath();
    void onAction(T item);
    Predicate<T> getCondition();
}

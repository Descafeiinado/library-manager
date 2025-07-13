package br.edu.ifba.inf008.core;

import javafx.scene.control.MenuItem;
import javafx.scene.Node;

public interface IUIController
{
    boolean createTab(String tabText, Node contents);
}

package br.edu.ifba.inf008.core.ui.models;

import javafx.scene.Node;

/**
 * Represents information about a tab in the user interface. This record holds the text label and an
 * icon for the tab.
 *
 * @param text The text label of the tab.
 * @param icon The icon associated with the tab, represented as a Node.
 */
public record TabInformation(String text, Node icon) {

}
package br.edu.ifba.inf008.plugins.reports.domain.entities;

import javafx.scene.Node;

public interface Report {

    String getId();

    String getName();

    Node getMainContent();
}

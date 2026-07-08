package com.cellularautomata.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/** A visual card representing a module selection. */
public class ModuleCard extends VBox {

  /**
   * Constructs a new module card.
   *
   * @param titleText the title of the module
   * @param descText the description of the module
   */
  public ModuleCard(String titleText, String descText) {
    getStyleClass().add("module-card");

    // Placeholder icon area
    Rectangle placeholder = new Rectangle(64, 64);
    placeholder.getStyleClass().add("module-card-icon-placeholder");

    Label title = new Label(titleText);
    title.getStyleClass().add("module-card-title");

    Label desc = new Label(descText);
    desc.getStyleClass().add("module-card-desc");
    desc.setWrapText(true);

    getChildren().addAll(placeholder, title, desc);
  }
}

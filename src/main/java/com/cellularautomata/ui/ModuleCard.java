package com.cellularautomata.ui;

import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** A visual card representing a module selection. */
public class ModuleCard extends VBox {

  private final RadioButton radio;

  /**
   * Constructs a new module card.
   *
   * @param titleText the title of the module
   * @param descText the description of the module
   */
  public ModuleCard(String titleText, String descText) {
    getStyleClass().add("module-card");

    // Header with radio button
    HBox headerBox = new HBox();
    radio = new RadioButton();
    radio.setMouseTransparent(true); // Let the card handle clicks
    headerBox.getChildren().add(radio);

    // Placeholder icon area
    Region placeholder = new Region();
    placeholder.getStyleClass().add("placeholder");
    placeholder.setMinSize(64, 64);
    placeholder.setMaxSize(64, 64);

    Label title = new Label(titleText);
    title.getStyleClass().addAll("label", "h2");

    Label desc = new Label(descText);
    desc.getStyleClass().add("module-card-desc");
    desc.setWrapText(true);

    getChildren().addAll(headerBox, placeholder, title, desc);
  }

  /**
   * Updates the visual selection state of the card.
   *
   * @param selected true if the card is selected
   */
  public void setSelected(boolean selected) {
    radio.setSelected(selected);
    if (selected) {
      if (!getStyleClass().contains("module-card-selected")) {
        getStyleClass().add("module-card-selected");
      }
    } else {
      getStyleClass().remove("module-card-selected");
    }
  }
}

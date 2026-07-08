package com.cellularautomata.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/** The application entry point screen for selecting a module. */
public class ModuleSelectionScreen extends VBox {

  /** Constructs the module selection screen. */
  public ModuleSelectionScreen() {
    getStyleClass().add("module-selection-screen");

    // Header
    VBox header = new VBox(UiStyles.APP_SPACING);
    header.setAlignment(Pos.CENTER);
    header.getStyleClass().add("module-selection-header");

    Label title = new Label("Choose a module");
    title.getStyleClass().addAll("label", "h2");

    Label subtitle =
        new Label(
            "Select the type of cellular automata you want to explore.\n"
                + "Each module has its own workspace, rules, and data.");
    subtitle.getStyleClass().add("module-selection-subtitle");
    subtitle.setAlignment(Pos.CENTER);
    subtitle.setTextAlignment(TextAlignment.CENTER);

    ProjectIcon icon = new ProjectIcon();
    header.getChildren().addAll(icon, title, subtitle);

    // Cards
    HBox cards = new HBox(UiStyles.APP_SPACING * 2);
    cards.setAlignment(Pos.CENTER);
    cards.getStyleClass().add("module-selection-cards");

    ModuleCard elementary =
        new ModuleCard(
            "Elementary", "Uses neighborhood patterns to determine the next state of a cell.");

    ModuleCard totalistic =
        new ModuleCard(
            "Totalistic", "Uses the sum of neighbor states to determine the next state of a cell.");

    cards.getChildren().addAll(elementary, totalistic);

    // Footer
    Label footerText = new Label("You can switch modules at any time from settings.");
    footerText.getStyleClass().add("module-selection-footer");

    getChildren().addAll(header, cards, footerText);
  }
}

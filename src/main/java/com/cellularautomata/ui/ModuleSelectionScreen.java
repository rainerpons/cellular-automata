package com.cellularautomata.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/** The application entry point screen for selecting a module. */
public class ModuleSelectionScreen extends VBox {

  private ModuleCard selectedCard;

  /** Constructs the module selection screen. */
  public ModuleSelectionScreen() {
    getStyleClass().add("module-selection-screen");

    // Header
    VBox header = new VBox(UiStyles.APP_SPACING);
    header.setAlignment(Pos.CENTER);
    header.getStyleClass().add("module-selection-header");

    Label title = new Label("Choose a module");
    title.getStyleClass().addAll("label", "h1");

    Label subtitle = new Label("Select the type of cellular automata you want to explore.");
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
        new ModuleCard("Elementary", "Uses nearby cells to determine the next cell state.");
    ModuleCard totalistic =
        new ModuleCard(
            "Totalistic",
            "Uses the sum of neighboring cell states to determine the next cell state.");

    elementary.setOnMouseClicked(e -> selectCard(elementary));
    totalistic.setOnMouseClicked(e -> selectCard(totalistic));

    cards.getChildren().addAll(elementary, totalistic);

    // Footer
    VBox footer = new VBox(UiStyles.APP_SPACING);
    footer.setAlignment(Pos.CENTER);

    Button continueButton = new Button("Continue");
    continueButton.getStyleClass().add("continue-button");
    continueButton.setPrefWidth(540); // Matches two 260 width cards + 20 spacing

    Label footerText = new Label("You can switch modules at any time from settings.");
    footerText.getStyleClass().add("module-selection-footer");

    footer.getChildren().addAll(continueButton, footerText);

    getChildren().addAll(header, cards, footer);
  }

  private void selectCard(ModuleCard card) {
    if (selectedCard != null) {
      selectedCard.setSelected(false);
    }
    selectedCard = card;
    selectedCard.setSelected(true);
  }
}

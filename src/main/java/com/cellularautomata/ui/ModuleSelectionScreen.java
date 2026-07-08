package com.cellularautomata.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/** The application entry point screen for selecting a module. */
public class ModuleSelectionScreen extends VBox {

  private static final double CONTINUE_BUTTON_WIDTH = 540;
  private static final double FOOTER_SPACING = 14;
  private static final double ICON_SIZE = 64;

  private final Button continueButton;
  private final Tooltip continueTooltip;
  private final StackPane buttonWrapper;

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

    ImageView icon =
        new ImageView(
            new Image(
                ModuleSelectionScreen.class.getResourceAsStream("/cellular-automata-icon.png")));
    icon.setFitWidth(ICON_SIZE);
    icon.setFitHeight(ICON_SIZE);
    icon.setPreserveRatio(true);

    header.getChildren().addAll(icon, title, subtitle);

    // Cards
    HBox cards = new HBox(UiStyles.APP_SPACING * 2);
    cards.setAlignment(Pos.CENTER);
    cards.getStyleClass().add("module-selection-cards");

    ModuleCard elementary =
        new ModuleCard("Elementary", "Uses nearby cells to determine the next cell state.");
    ModuleCard totalistic =
        new ModuleCard(
            "Totalistic", "Uses the sum of nearby cell states to determine the next cell state.");

    cards.getChildren().addAll(elementary, totalistic);

    // Footer
    VBox footer = new VBox(FOOTER_SPACING);
    footer.setAlignment(Pos.CENTER);

    continueButton = new Button("Continue");
    continueButton.getStyleClass().add("continue-button");
    continueButton.setPrefWidth(CONTINUE_BUTTON_WIDTH); // Matches two 260 width cards + 20 spacing
    continueButton.setDisable(true);

    buttonWrapper = new StackPane(continueButton);
    continueTooltip = new Tooltip("Select a module to continue.");
    continueTooltip.getStyleClass().add("tooltip");
    Tooltip.install(buttonWrapper, continueTooltip);

    elementary.setOnMouseClicked(e -> handleCardSelection(elementary));
    totalistic.setOnMouseClicked(e -> handleCardSelection(totalistic));

    Label footerText = new Label("You can switch modules at any time from settings.");
    footerText.getStyleClass().add("module-selection-footer");

    footer.getChildren().addAll(buttonWrapper, footerText);

    getChildren().addAll(header, cards, footer);
  }

  private void handleCardSelection(ModuleCard card) {
    if (selectedCard != null) {
      selectedCard.setSelected(false);
    }
    selectedCard = card;
    selectedCard.setSelected(true);

    continueButton.setDisable(false);
    Tooltip.uninstall(buttonWrapper, continueTooltip);
  }
}

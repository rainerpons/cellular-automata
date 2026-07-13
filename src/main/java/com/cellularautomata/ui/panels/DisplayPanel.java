package com.cellularautomata.ui.panels;

import com.cellularautomata.ui.shared.UiStyles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * The left column of the application window. Displays the section heading and the generated
 * automaton image, or a placeholder prompt before generation.
 */
public final class DisplayPanel extends VBox {
  private final VBox instructionsBox;
  private final ImageView automatonImageView;
  private final StackPane imageContainer;

  /**
   * Constructs the display panel.
   *
   * @param config the workspace configuration
   */
  public DisplayPanel(com.cellularautomata.config.WorkspaceConfig config) {

    // Add section heading.
    Label displayHeading = UiStyles.createHeading("Display");
    VBox.setMargin(displayHeading, new Insets(0, 0, UiStyles.DISPLAY_HEADING_GAP, 0));
    getChildren().add(displayHeading);

    // Add placeholder label, replaced by the automaton image after generation.
    instructionsBox = new VBox();
    instructionsBox.setAlignment(Pos.CENTER);
    instructionsBox.setSpacing(10); // Reduce spacing by ~33% compared to double newline

    Label promptLabel = new Label("Click the Generate automaton button to begin.");
    promptLabel.setTextAlignment(TextAlignment.CENTER);
    promptLabel.getStyleClass().add("placeholder");

    String paramsText =
        config == com.cellularautomata.config.WorkspaceConfig.TOTALISTIC
            ? "Select size, states, rule, and seed type."
            : "Select size, rule, and seed type.";
    Label paramsLabel = new Label(paramsText);
    paramsLabel.setTextAlignment(TextAlignment.CENTER);
    paramsLabel.getStyleClass().add("placeholder");

    instructionsBox.getChildren().addAll(promptLabel, paramsLabel);
    instructionsBox.setMinSize(400, 400);
    instructionsBox.setPrefSize(400, 400);
    instructionsBox.setMaxSize(400, 400);

    automatonImageView = new ImageView();
    automatonImageView.setPreserveRatio(true);

    imageContainer = new StackPane();
    imageContainer.setMinSize(400, 400);
    imageContainer.setPrefSize(400, 400);
    imageContainer.setMaxSize(400, 400);
    imageContainer.getChildren().add(instructionsBox);

    getChildren().add(imageContainer);
  }

  /**
   * Sets the automaton image.
   *
   * @param image the image
   */
  public void setAutomatonImage(Image image) {
    imageContainer.getChildren().remove(instructionsBox);
    automatonImageView.setImage(image);
    if (!imageContainer.getChildren().contains(automatonImageView)) {
      imageContainer.getChildren().add(automatonImageView);
    }
  }
}

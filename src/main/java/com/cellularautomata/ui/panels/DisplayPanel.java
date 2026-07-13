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
  private final Label placeholderLabel;
  private final ImageView automatonImageView;
  private final StackPane imageContainer;

  /** Constructs the display panel. */
  public DisplayPanel() {
    // Add section heading.
    Label displayHeading = UiStyles.createHeading("Display");
    VBox.setMargin(displayHeading, new Insets(0, 0, UiStyles.DISPLAY_HEADING_GAP, 0));
    getChildren().add(displayHeading);

    // Add placeholder label, replaced by the automaton image after generation.
    placeholderLabel =
        new Label(
            "Click the Generate automaton button to begin.\n\nSelect size, rule, and seed type.");
    placeholderLabel.setTextAlignment(TextAlignment.CENTER);
    placeholderLabel.setAlignment(Pos.CENTER);
    placeholderLabel.getStyleClass().add("placeholder");
    placeholderLabel.setMinSize(400, 400);
    placeholderLabel.setPrefSize(400, 400);
    placeholderLabel.setMaxSize(400, 400);

    automatonImageView = new ImageView();
    automatonImageView.setPreserveRatio(true);

    imageContainer = new StackPane();
    imageContainer.setMinSize(400, 400);
    imageContainer.setPrefSize(400, 400);
    imageContainer.setMaxSize(400, 400);
    imageContainer.getChildren().add(placeholderLabel);

    getChildren().add(imageContainer);
  }

  /**
   * Sets the automaton image.
   *
   * @param image the image
   */
  public void setAutomatonImage(Image image) {
    imageContainer.getChildren().remove(placeholderLabel);
    automatonImageView.setImage(image);
    if (!imageContainer.getChildren().contains(automatonImageView)) {
      imageContainer.getChildren().add(automatonImageView);
    }
  }
}

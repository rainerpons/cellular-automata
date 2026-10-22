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
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;

/**
 * The left column of the application window. Displays the section heading and the generated
 * automaton image, or a placeholder prompt before generation, or an error view upon failure.
 */
public final class DisplayPanel extends VBox {
  public static final String DEFAULT_PLACEHOLDER_TEXT =
      "Click the Generate automaton button to begin.\n\nSelect size, rule, and seed type.";
  public static final String LIFE_LIKE_PLACEHOLDER_TEXT =
      "Click Play or Step to begin.\n\nSelect size, preset, seed type, and birth/survival rules.";

  public static final String GENERATION_ERROR_PRIMARY = "Failed to generate automaton.";
  public static final String RENDERING_ERROR_PRIMARY = "Failed to render automaton.";
  public static final String ERROR_SECONDARY = "Please try again.";

  private static final int ERROR_ICON_SIZE = 40;
  private static final int ERROR_SPACING = 8;

  private final Label placeholderLabel;
  private final ImageView automatonImageView;
  private final StackPane imageContainer;
  private final VBox errorContainer;
  private final Label primaryErrorLabel;
  private final Label secondaryErrorLabel;

  /** Constructs the display panel with the default 1D generation placeholder text. */
  public DisplayPanel() {
    this(DEFAULT_PLACEHOLDER_TEXT);
  }

  /**
   * Constructs the display panel with the specified placeholder text.
   *
   * @param placeholderText the initial prompt displayed before an automaton is shown
   */
  public DisplayPanel(String placeholderText) {
    // Add section heading.
    Label displayHeading = UiStyles.createHeading("Display");
    VBox.setMargin(displayHeading, new Insets(0, 0, UiStyles.DISPLAY_HEADING_GAP, 0));
    getChildren().add(displayHeading);

    // Add placeholder label, replaced by the automaton image after generation.
    placeholderLabel = new Label(placeholderText);
    placeholderLabel.setTextAlignment(TextAlignment.CENTER);
    placeholderLabel.setAlignment(Pos.CENTER);
    placeholderLabel.getStyleClass().add("placeholder");
    placeholderLabel.setMinSize(400, 400);
    placeholderLabel.setPrefSize(400, 400);
    placeholderLabel.setMaxSize(400, 400);

    automatonImageView = new ImageView();
    automatonImageView.setPreserveRatio(true);

    FontIcon errorIcon = new FontIcon(MaterialDesignA.ALERT_CIRCLE_OUTLINE);
    errorIcon.setIconSize(ERROR_ICON_SIZE);
    errorIcon.getStyleClass().add("error-icon");

    primaryErrorLabel = new Label();
    primaryErrorLabel.setTextAlignment(TextAlignment.CENTER);
    primaryErrorLabel.setAlignment(Pos.CENTER);
    primaryErrorLabel.getStyleClass().add("error-label");

    secondaryErrorLabel = new Label();
    secondaryErrorLabel.setTextAlignment(TextAlignment.CENTER);
    secondaryErrorLabel.setAlignment(Pos.CENTER);
    secondaryErrorLabel.getStyleClass().add("error-label");

    errorContainer = new VBox(ERROR_SPACING);
    errorContainer.setAlignment(Pos.CENTER);
    errorContainer.getStyleClass().addAll("placeholder", "error-container");
    errorContainer.setMinSize(400, 400);
    errorContainer.setPrefSize(400, 400);
    errorContainer.setMaxSize(400, 400);
    errorContainer.getChildren().addAll(errorIcon, primaryErrorLabel, secondaryErrorLabel);

    imageContainer = new StackPane();
    imageContainer.setMinSize(400, 400);
    imageContainer.setPrefSize(400, 400);
    imageContainer.setMaxSize(400, 400);
    imageContainer.getChildren().add(placeholderLabel);

    getChildren().add(imageContainer);
  }

  /**
   * Sets the automaton image, clearing any active placeholder or error view.
   *
   * @param image the image to display
   */
  public void setAutomatonImage(Image image) {
    if (image == null) {
      showPlaceholder();
      return;
    }
    imageContainer.getChildren().remove(placeholderLabel);
    imageContainer.getChildren().remove(errorContainer);
    automatonImageView.setImage(image);
    if (!imageContainer.getChildren().contains(automatonImageView)) {
      imageContainer.getChildren().add(automatonImageView);
    }
  }

  /**
   * Displays an error view with the given primary and secondary messages.
   *
   * @param primaryMessage primary error line
   * @param secondaryMessage secondary error line
   */
  public void showError(String primaryMessage, String secondaryMessage) {
    primaryErrorLabel.setText(primaryMessage);
    secondaryErrorLabel.setText(secondaryMessage);
    imageContainer.getChildren().remove(placeholderLabel);
    imageContainer.getChildren().remove(automatonImageView);
    if (!imageContainer.getChildren().contains(errorContainer)) {
      imageContainer.getChildren().add(errorContainer);
    }
  }

  /** Displays the standard automaton generation failure error view. */
  public void showGenerationError() {
    showError(GENERATION_ERROR_PRIMARY, ERROR_SECONDARY);
  }

  /** Displays the standard automaton rendering failure error view. */
  public void showRenderingError() {
    showError(RENDERING_ERROR_PRIMARY, ERROR_SECONDARY);
  }

  /** Clears the error view, restoring either the active image or the placeholder. */
  public void clearError() {
    if (!isDisplayingError()) {
      return;
    }
    imageContainer.getChildren().remove(errorContainer);
    if (automatonImageView.getImage() != null) {
      if (!imageContainer.getChildren().contains(automatonImageView)) {
        imageContainer.getChildren().add(automatonImageView);
      }
      return;
    }
    if (!imageContainer.getChildren().contains(placeholderLabel)) {
      imageContainer.getChildren().add(placeholderLabel);
    }
  }

  /** Restores the placeholder view and clears any active image and error. */
  public void showPlaceholder() {
    imageContainer.getChildren().remove(errorContainer);
    imageContainer.getChildren().remove(automatonImageView);
    automatonImageView.setImage(null);
    if (!imageContainer.getChildren().contains(placeholderLabel)) {
      imageContainer.getChildren().add(placeholderLabel);
    }
  }

  public boolean isDisplayingError() {
    return imageContainer.getChildren().contains(errorContainer);
  }

  public String getErrorMessage() {
    if (!isDisplayingError()) {
      return null;
    }
    String primary = primaryErrorLabel.getText();
    String secondary = secondaryErrorLabel.getText();
    if (primary == null && secondary == null) {
      return null;
    }
    if (primary == null) {
      return secondary;
    }
    if (secondary == null || secondary.isBlank()) {
      return primary;
    }
    return primary + "\n" + secondary;
  }

  public String getPrimaryErrorMessage() {
    return isDisplayingError() ? primaryErrorLabel.getText() : null;
  }

  public String getSecondaryErrorMessage() {
    return isDisplayingError() ? secondaryErrorLabel.getText() : null;
  }

  public Image getAutomatonImage() {
    return automatonImageView.getImage();
  }

  public String getPlaceholderText() {
    return placeholderLabel.getText();
  }
}

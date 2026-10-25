package com.cellularautomata.ui.panels;

import com.cellularautomata.image.AutomatonImage;
import com.cellularautomata.ui.shared.UiStyles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
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
    placeholderLabel = createCenteredLabel(placeholderText, "placeholder");
    applyDisplayBounds(placeholderLabel);

    automatonImageView = new ImageView();
    automatonImageView.setPreserveRatio(true);

    FontIcon errorIcon = new FontIcon(MaterialDesignA.ALERT_CIRCLE_OUTLINE);
    errorIcon.setIconSize(ERROR_ICON_SIZE);
    errorIcon.getStyleClass().add("error-icon");

    primaryErrorLabel = createCenteredLabel(null, "error-label");
    secondaryErrorLabel = createCenteredLabel(null, "error-label");

    errorContainer = new VBox(ERROR_SPACING);
    errorContainer.setAlignment(Pos.CENTER);
    errorContainer.getStyleClass().addAll("placeholder", "error-container");
    applyDisplayBounds(errorContainer);
    errorContainer.getChildren().addAll(errorIcon, primaryErrorLabel, secondaryErrorLabel);

    imageContainer = new StackPane();
    applyDisplayBounds(imageContainer);
    imageContainer.getChildren().setAll(placeholderLabel);

    getChildren().add(imageContainer);
  }

  private static void applyDisplayBounds(Region region) {
    region.setMinSize(AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE);
    region.setPrefSize(AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE);
    region.setMaxSize(AutomatonImage.DISPLAY_IMAGE_SIZE, AutomatonImage.DISPLAY_IMAGE_SIZE);
  }

  private static Label createCenteredLabel(String text, String... styleClasses) {
    Label label = text == null ? new Label() : new Label(text);
    label.setTextAlignment(TextAlignment.CENTER);
    label.setAlignment(Pos.CENTER);
    if (styleClasses != null && styleClasses.length > 0) {
      label.getStyleClass().addAll(styleClasses);
    }
    return label;
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
    automatonImageView.setImage(image);
    imageContainer.getChildren().setAll(automatonImageView);
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
    imageContainer.getChildren().setAll(errorContainer);
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
    if (automatonImageView.getImage() != null) {
      imageContainer.getChildren().setAll(automatonImageView);
      return;
    }
    imageContainer.getChildren().setAll(placeholderLabel);
  }

  /** Restores the placeholder view and clears any active image and error. */
  public void showPlaceholder() {
    automatonImageView.setImage(null);
    imageContainer.getChildren().setAll(placeholderLabel);
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

  public Image getAutomatonImage() {
    return automatonImageView.getImage();
  }

  public String getPlaceholderText() {
    return placeholderLabel.getText();
  }
}

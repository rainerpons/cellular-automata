package com.cellularautomata.ui.components;

import com.cellularautomata.ui.WorkspaceConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** A visual card representing a module selection. */
public final class ModuleCard extends StackPane {

  private static final double IMAGE_SIZE = 64;
  private static final Insets RADIO_BUTTON_MARGIN = new Insets(-15, 0, 0, -15);

  private final RadioButton radio;
  private final WorkspaceConfig config;

  /**
   * Constructs a new module card.
   *
   * @param config the configuration of the workspace
   * @param titleText the title of the module
   * @param descText the description of the module
   * @param imageResourcePath classpath resource path for the module preview image
   */
  public ModuleCard(
      WorkspaceConfig config, String titleText, String descText, String imageResourcePath) {
    this.config = config;
    getStyleClass().add("module-card");

    radio = new RadioButton();
    radio.setMouseTransparent(true); // Let the card handle clicks
    StackPane.setAlignment(radio, Pos.TOP_LEFT);
    StackPane.setMargin(radio, RADIO_BUTTON_MARGIN);

    VBox content = new VBox();
    content.getStyleClass().add("module-card-content");

    ImageView imageView = createImageView(imageResourcePath);
    StackPane imageContainer = new StackPane(imageView);
    imageContainer.getStyleClass().add("module-card-image-container");

    Label title = new Label(titleText);
    title.getStyleClass().addAll("label", "h2");

    Label desc = new Label(descText);
    desc.getStyleClass().add("module-card-desc");
    desc.setWrapText(true);
    desc.setMinHeight(Region.USE_PREF_SIZE);

    content.getChildren().addAll(imageContainer, title, desc);
    getChildren().addAll(radio, content);
  }

  private static ImageView createImageView(String imageResourcePath) {
    if (imageResourcePath == null || imageResourcePath.isBlank()) {
      throw new IllegalArgumentException("Module card image resource path must not be blank");
    }
    try (InputStream stream = ModuleCard.class.getResourceAsStream(imageResourcePath)) {
      if (stream == null) {
        throw new IllegalArgumentException(
            "Module card image resource not found: %s".formatted(imageResourcePath));
      }
      ImageView imageView = new ImageView(new Image(stream));
      imageView.setFitWidth(IMAGE_SIZE);
      imageView.setFitHeight(IMAGE_SIZE);
      imageView.setPreserveRatio(true);
      imageView.getStyleClass().add("module-card-image");
      return imageView;
    } catch (IOException e) {
      throw new UncheckedIOException(
          "Failed to load module card image: %s".formatted(imageResourcePath), e);
    }
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

  public WorkspaceConfig getConfig() {
    return config;
  }
}

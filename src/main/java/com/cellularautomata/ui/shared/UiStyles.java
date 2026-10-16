package com.cellularautomata.ui.shared;

import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Shared styling constants and component helpers for the UI package. Centralises spacing, sizing,
 * and appearance values so individual panels stay focused on layout structure.
 */
public final class UiStyles {
  // Heading and component styling
  public static final String HEADING_STYLE_CLASS = "h3";
  public static final String TOOLTIP_STYLE_CLASS = "tooltip";
  public static final int CONTROL_HEIGHT = 35;
  public static final int FORM_LABEL_COLUMN_MIN_WIDTH = 95;

  // Vertical and horizontal spacing
  public static final int HEADING_BOTTOM_GAP = 10;
  public static final int BUTTON_GAP_X = 5;
  public static final int DISPLAY_HEADING_GAP = 19;
  public static final int SECTION_HEADING_GAP = 19;
  public static final int PARAMETERS_HEADING_GAP = 17;
  public static final int LABEL_TO_CONTROL_GAP = 6;
  public static final int CONTROL_GROUP_GAP = 21;
  public static final int FORM_ROW_BOTTOM_GAP = 15;
  public static final int FORM_LABEL_COLUMN_GAP = 18;
  public static final int SECTION_SEPARATOR_TOP_GAP = 32;
  public static final int SECTION_SEPARATOR_BOTTOM_GAP = 28;

  // Root layout spacing
  public static final int APP_SPACING = 21;

  private UiStyles() {}

  // Component factories and styling helpers
  /**
   * Creates a heading label.
   *
   * @param text the text
   * @return the label
   */
  public static Label createHeading(String text) {
    Label heading = new Label(text);
    heading.getStyleClass().add(HEADING_STYLE_CLASS);
    return heading;
  }

  /**
   * Applies control height.
   *
   * @param component the component
   */
  public static void applyControlHeight(Control component) {
    component.setMinHeight(CONTROL_HEIGHT);
    component.setPrefHeight(CONTROL_HEIGHT);
  }

  /**
   * Creates a styled tooltip with the given text.
   *
   * @param text the tooltip text
   * @return the styled tooltip
   */
  public static Tooltip createTooltip(String text) {
    Tooltip tooltip = new Tooltip(text);
    tooltip.getStyleClass().add(TOOLTIP_STYLE_CLASS);
    return tooltip;
  }

  /**
   * Creates and attaches a styled tooltip to a control.
   *
   * @param control the control to attach the tooltip to
   * @param text the tooltip text
   * @return the installed tooltip
   */
  public static Tooltip installTooltip(Control control, String text) {
    Tooltip tooltip = createTooltip(text);
    control.setTooltip(tooltip);
    return tooltip;
  }

  /**
   * Creates and installs a styled tooltip on an arbitrary scene graph node.
   *
   * @param node the target node
   * @param text the tooltip text
   * @return the installed tooltip
   */
  public static Tooltip installTooltip(Node node, String text) {
    Tooltip tooltip = createTooltip(text);
    Tooltip.install(node, tooltip);
    return tooltip;
  }

  /**
   * Creates a full-width dropdown control populated with the given items and default selection.
   *
   * @param <T> the item type
   * @param items the selectable items
   * @param defaultValue the initial selected item
   * @return the configured combo box
   */
  public static <T> ComboBox<T> createDropdown(Collection<T> items, T defaultValue) {
    ComboBox<T> comboBox = new ComboBox<>(FXCollections.observableArrayList(items));
    comboBox.setValue(defaultValue);
    applyControlHeight(comboBox);
    comboBox.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(comboBox, Priority.ALWAYS);
    return comboBox;
  }
}

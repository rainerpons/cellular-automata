package com.cellularautomata.ui.shared;

import javafx.scene.control.Control;
import javafx.scene.control.Label;

/**
 * Shared styling constants and component helpers for the UI package. Centralises spacing, sizing,
 * and appearance values so individual panels stay focused on layout structure.
 */
public final class UiStyles {
  // Heading and component styling
  public static final String HEADING_STYLE_CLASS = "h3";
  public static final int CONTROL_HEIGHT = 35;

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
}

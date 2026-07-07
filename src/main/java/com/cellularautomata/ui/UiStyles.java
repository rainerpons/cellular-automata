package com.cellularautomata.ui;

import javafx.scene.control.Control;
import javafx.scene.control.Label;

/**
 * Shared styling constants and component helpers for the UI package. Centralises spacing, sizing,
 * and appearance values so individual panels stay focused on layout structure.
 */
final class UiStyles {
  // Heading and component styling
  static final String HEADING_STYLE_CLASS = "h3";
  static final int CONTROL_HEIGHT = 35;

  // Vertical and horizontal spacing
  static final int HEADING_BOTTOM_GAP = 10;
  static final int BUTTON_GAP_X = 5;
  static final int DISPLAY_HEADING_GAP = 16;
  static final int SECTION_HEADING_GAP = 16;
  static final int PARAMETERS_HEADING_GAP = 14;
  static final int LABEL_TO_CONTROL_GAP = 5;
  static final int CONTROL_GROUP_GAP = 18;
  static final int FORM_ROW_BOTTOM_GAP = 12;
  static final int FORM_LABEL_COLUMN_GAP = 16;
  static final int SECTION_SEPARATOR_TOP_GAP = 28;
  static final int SECTION_SEPARATOR_BOTTOM_GAP = 24;

  // Root layout spacing
  static final int APP_SPACING = 18;

  private UiStyles() {}

  // Component factories and styling helpers
  static Label createHeading(String text) {
    Label heading = new Label(text);
    heading.getStyleClass().add(HEADING_STYLE_CLASS);
    return heading;
  }

  static void applyControlHeight(Control component) {
    component.setMinHeight(CONTROL_HEIGHT);
    component.setPrefHeight(CONTROL_HEIGHT);
  }
}

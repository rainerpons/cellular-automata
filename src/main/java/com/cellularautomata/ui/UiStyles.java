package com.cellularautomata.ui;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JLabel;

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
  static final int DISPLAY_HEADING_GAP = 15;
  static final int SECTION_HEADING_GAP = 15;
  static final int LABEL_TO_CONTROL_GAP = 5;
  static final int CONTROL_GROUP_GAP = 15;
  static final int FORM_ROW_BOTTOM_GAP = 10;
  static final int FORM_LABEL_COLUMN_GAP = 15;
  static final int SECTION_SEPARATOR_GAP = 16;

  // Root layout spacing
  static final int APP_SPACING = 16;

  private UiStyles() {}

  // Component factories and styling helpers
  static JLabel createHeading(String text) {
    JLabel heading = new JLabel(text);
    heading.putClientProperty("FlatLaf.styleClass", HEADING_STYLE_CLASS);
    return heading;
  }

  static void applyControlHeight(JComponent component) {
    Dimension prefSize = component.getPreferredSize();
    component.setPreferredSize(new Dimension(prefSize.width, CONTROL_HEIGHT));
    Dimension minSize = component.getMinimumSize();
    component.setMinimumSize(new Dimension(minSize.width, CONTROL_HEIGHT));
  }
}

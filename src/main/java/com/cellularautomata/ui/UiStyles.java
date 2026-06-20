package com.cellularautomata.ui;

import javax.swing.JLabel;

final class UiStyles {
  static final int HEADING_BOTTOM_GAP = 10;
  static final int BUTTON_GAP_X = 5;
  static final int DISPLAY_HEADING_GAP = 15;
  static final int SECTION_HEADING_GAP = 15;
  static final int LABEL_TO_CONTROL_GAP = 5;
  static final int CONTROL_GROUP_GAP = 15;
  static final int FORM_ROW_BOTTOM_GAP = 10;
  static final int FORM_LABEL_COLUMN_GAP = 15;

  static final int SIDEBAR_PADDING_TOP = 20;
  static final int SIDEBAR_PADDING_LEFT = 10;
  static final int SIDEBAR_PADDING_BOTTOM = 20;
  static final int SIDEBAR_PADDING_RIGHT = 20;

  static final int SECTION_VERTICAL_GAP = 20;

  private UiStyles() {}

  static JLabel createHeading(String text) {
    JLabel heading = new JLabel(text);
    heading.putClientProperty("FlatLaf.styleClass", "h3");
    return heading;
  }
}

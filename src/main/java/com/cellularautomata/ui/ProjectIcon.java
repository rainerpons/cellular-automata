package com.cellularautomata.ui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/** Natively renders the project icon using JavaFX shapes. */
public class ProjectIcon extends Group {

  /** Constructs the project icon. */
  public ProjectIcon() {
    Color color = Color.web("#4ADE80");
    double size = 5;
    double arc = 1.5;

    // Central 3x3 cell cluster
    for (int x = 24; x <= 36; x += 6) {
      for (int y = 24; y <= 36; y += 6) {
        addRect(x, y, size, arc, color);
      }
    }

    // Diagonal propagation cells
    addRect(18, 18, size, arc, color);
    addRect(42, 18, size, arc, color);
    addRect(18, 42, size, arc, color);
    addRect(42, 42, size, arc, color);

    // Outer corner colonies
    addRect(8, 8, size, arc, color);
    addRect(14, 8, size, arc, color);
    addRect(8, 14, size, arc, color);

    addRect(45, 8, size, arc, color);
    addRect(51, 8, size, arc, color);
    addRect(51, 14, size, arc, color);

    addRect(8, 45, size, arc, color);
    addRect(8, 51, size, arc, color);
    addRect(14, 51, size, arc, color);

    addRect(51, 45, size, arc, color);
    addRect(45, 51, size, arc, color);
    addRect(51, 51, size, arc, color);
  }

  private void addRect(double x, double y, double size, double arc, Color color) {
    Rectangle rect = new Rectangle(x, y, size, size);
    rect.setArcWidth(arc);
    rect.setArcHeight(arc);
    rect.setFill(color);
    getChildren().add(rect);
  }
}

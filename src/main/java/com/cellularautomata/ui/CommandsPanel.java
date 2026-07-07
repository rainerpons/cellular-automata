package com.cellularautomata.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * The commands section of the sidebar. Contains the Generate and Save action buttons and exposes
 * listener registration methods so MainApp can wire up behavior without knowing about the button
 * internals.
 */
final class CommandsPanel extends GridPane {
  private final Button displayButton;
  private final Button saveButton;

  CommandsPanel() {
    setHgap(UiStyles.BUTTON_GAP_X);

    // Add section heading.
    final Label commandsHeading = UiStyles.createHeading("Commands");
    GridPane.setColumnSpan(commandsHeading, 2);
    GridPane.setMargin(commandsHeading, new Insets(0, 0, UiStyles.SECTION_HEADING_GAP, 0));
    add(commandsHeading, 0, 0);

    // Add command buttons.
    displayButton = new Button("Generate automaton");
    UiStyles.applyControlHeight(displayButton);
    displayButton.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(displayButton, Priority.ALWAYS);
    add(displayButton, 0, 1);

    saveButton = new Button("Save image");
    UiStyles.applyControlHeight(saveButton);
    saveButton.setDisable(true);
    saveButton.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(saveButton, Priority.ALWAYS);
    add(saveButton, 1, 1);
  }

  void addGenerateListener(EventHandler<ActionEvent> listener) {
    displayButton.setOnAction(listener);
  }

  void addSaveListener(EventHandler<ActionEvent> listener) {
    saveButton.setOnAction(listener);
  }

  void setSaveEnabled(boolean enabled) {
    saveButton.setDisable(!enabled);
  }
}

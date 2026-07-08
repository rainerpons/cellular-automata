package com.cellularautomata.ui;

import javafx.scene.control.Alert;

/** Service responsible for creating and displaying dialogs to the user. */
public class DialogService {

  /** Displays an error dialog indicating that the rule number is invalid. */
  public void showRuleError() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Rule Number Error");
    alert.setHeaderText(null);
    alert.setContentText("Rule must be a whole number between 0 and 255.");

    // Apply the application stylesheet to the dialog
    alert
        .getDialogPane()
        .getStylesheets()
        .add(getClass().getResource("/css/style.css").toExternalForm());

    if (alert.getDialogPane().getScene() != null) {
      alert.getDialogPane().getScene().setFill(javafx.scene.paint.Color.web("#3c3f41"));
    }

    alert.showAndWait();
  }

  /** Displays an error dialog indicating that the image could not be saved. */
  public void showSaveError() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Save Failed");
    alert.setHeaderText(null);
    alert.setContentText(
        "The automaton image could not be saved.\n"
            + "Please check that the selected location is writable and try again.");

    alert
        .getDialogPane()
        .getStylesheets()
        .add(getClass().getResource("/css/style.css").toExternalForm());

    if (alert.getDialogPane().getScene() != null) {
      alert.getDialogPane().getScene().setFill(javafx.scene.paint.Color.web("#3c3f41"));
    }

    alert.showAndWait();
  }
}

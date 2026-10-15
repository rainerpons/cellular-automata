package com.cellularautomata.ui;

import javafx.scene.control.Alert;

/** Service responsible for creating and displaying dialogs to the user. */
public class DialogService {

  /**
   * Displays an error dialog indicating that the rule number is invalid.
   *
   * @param minRule the minimum valid rule number
   * @param maxRule the maximum valid rule number
   */
  public void showRuleError(int minRule, int maxRule) {
    Alert alert =
        createStyledErrorDialog(
            "Rule Number Error",
            "Rule must be a whole number between " + minRule + " and " + maxRule + ".");
    alert.showAndWait();
  }

  /**
   * Displays an error dialog indicating that the states number is invalid. This is a defensive
   * safeguard supporting the manual entry edge case from MainController where invalid values are
   * entered into the spinner's text box.
   *
   * @param minStates the minimum valid states number
   * @param maxStates the maximum valid states number
   */
  public void showStatesError(int minStates, int maxStates) {
    Alert alert =
        createStyledErrorDialog(
            "States Error",
            "States must be a whole number between " + minStates + " and " + maxStates + ".");
    alert.showAndWait();
  }

  /** Displays an error dialog indicating that the image could not be saved. */
  public void showSaveError() {
    Alert alert =
        createStyledErrorDialog(
            "Save Failed",
            "The automaton image could not be saved.\n"
                + "Please check that the selected location is writable and try again.");
    alert.showAndWait();
  }

  private Alert createStyledErrorDialog(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);

    // Apply the application stylesheet to the dialog
    alert
        .getDialogPane()
        .getStylesheets()
        .add(getClass().getResource("/css/style.css").toExternalForm());

    if (alert.getDialogPane().getScene() != null) {
      alert.getDialogPane().getScene().setFill(javafx.scene.paint.Color.web("#3c3f41"));
    }

    return alert;
  }
}

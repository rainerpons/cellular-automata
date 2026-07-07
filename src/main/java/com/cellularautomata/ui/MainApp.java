package com.cellularautomata.ui;

import com.cellularautomata.engine.AutomataEngine;
import com.cellularautomata.engine.AutomataResult;
import com.cellularautomata.engine.RuleValidator;
import com.cellularautomata.engine.Vector;
import com.cellularautomata.image.AutomatonImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.OptionalInt;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The MainApp class owns JavaFX UI setup and event wiring, while delegating generation and image
 * work.
 */
public class MainApp extends Application {
  private BufferedImage resizedAutomatonImage;
  private Vector seed;
  private int rule;

  private DisplayPanel displayPanel = new DisplayPanel();
  private ParametersPanel parametersPanel = new ParametersPanel();
  private CommandsPanel commandsPanel = new CommandsPanel();
  private SidebarPanel sidebarPanel;

  @Override
  public void start(Stage primaryStage) {
    UiFonts.registerBundledFonts();

    primaryStage.setTitle("Cellular Automata");
    primaryStage.setResizable(false);

    sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);

    HBox root = new HBox(UiStyles.APP_SPACING);
    root.setPadding(new Insets(UiStyles.APP_SPACING));
    HBox.setHgrow(sidebarPanel, javafx.scene.layout.Priority.ALWAYS);
    root.getChildren().addAll(displayPanel, sidebarPanel);

    setupActionListeners();

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void setupActionListeners() {
    commandsPanel.addGenerateListener(e -> generateAutomaton());
    commandsPanel.addSaveListener(e -> saveAutomatonImage());
  }

  private void generateAutomaton() {
    OptionalInt parsedRule = RuleValidator.parseRule(parametersPanel.getRuleText());
    if (!parsedRule.isPresent()) {
      showRuleError();
      return;
    }

    rule = parsedRule.getAsInt();
    AutomataResult result =
        AutomataEngine.generate(
            rule, parametersPanel.getSizeValue(), parametersPanel.getSeedType());
    seed = result.getOriginalSeed();

    BufferedImage automatonImage = AutomatonImage.getImageFromMap(result.getAutomatonMap());
    resizedAutomatonImage = AutomatonImage.resizeImage(400, 400, automatonImage);

    displayPanel.setAutomatonImage(SwingFXUtils.toFXImage(resizedAutomatonImage, null));
    commandsPanel.setSaveEnabled(true);
  }

  private void saveAutomatonImage() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialFileName(AutomatonImage.getFileName(rule, seed));
    javafx.stage.Window window = displayPanel.getScene().getWindow();
    File file = fileChooser.showSaveDialog(window);
    if (file == null) {
      return;
    }

    try {
      AutomatonImage.saveImage(resizedAutomatonImage, file);
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }

  private void showRuleError() {
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
}

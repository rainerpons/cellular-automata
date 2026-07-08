package com.cellularautomata.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * The MainApp class owns JavaFX UI setup and event wiring, while delegating generation and image
 * work.
 */
public class MainApp extends Application {

  private DisplayPanel displayPanel = new DisplayPanel();
  private ParametersPanel parametersPanel = new ParametersPanel();
  private CommandsPanel commandsPanel = new CommandsPanel();
  private SidebarPanel sidebarPanel;
  private DialogService dialogService = new DialogService();
  private MainController mainController;

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

    // Initialize the controller to wire everything up
    mainController =
        new MainController(displayPanel, parametersPanel, commandsPanel, dialogService);

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}

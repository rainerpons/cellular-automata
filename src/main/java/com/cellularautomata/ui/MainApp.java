package com.cellularautomata.ui;

import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ParametersPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
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

  private final DisplayPanel displayPanel = new DisplayPanel();
  private final ParametersPanel parametersPanel = new ParametersPanel();
  private final CommandsPanel commandsPanel = new CommandsPanel();
  private final SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);
  private final DialogService dialogService = new DialogService();

  /** Constructs the main application and wires up the controller. */
  public MainApp() {
    new MainController(displayPanel, parametersPanel, commandsPanel, dialogService);
  }

  @Override
  public void start(Stage primaryStage) {
    UiFonts.registerBundledFonts();

    primaryStage.setTitle("Cellular Automata");
    primaryStage.setResizable(false);

    ModuleSelectionScreen moduleSelectionScreen = new ModuleSelectionScreen();
    Scene scene = new Scene(moduleSelectionScreen);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @SuppressWarnings("unused")
  private Scene createElementaryWorkspace() {
    HBox root = new HBox(UiStyles.APP_SPACING);
    root.setPadding(new Insets(UiStyles.APP_SPACING));
    HBox.setHgrow(sidebarPanel, javafx.scene.layout.Priority.ALWAYS);
    root.getChildren().addAll(displayPanel, sidebarPanel);

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    return scene;
  }
}

package com.cellularautomata.ui;

import com.cellularautomata.ui.panels.CommandsPanel;
import com.cellularautomata.ui.panels.DisplayPanel;
import com.cellularautomata.ui.panels.ParametersPanel;
import com.cellularautomata.ui.panels.SidebarPanel;
import com.cellularautomata.ui.shared.UiStyles;
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

  private final DialogService dialogService = new DialogService();

  /** Constructs the main application. */
  public MainApp() {}

  @Override
  public void start(Stage primaryStage) {
    UiFonts.registerBundledFonts();

    primaryStage.setTitle("Cellular Automata");
    primaryStage.setResizable(false);

    ModuleSelectionScreen moduleSelectionScreen = new ModuleSelectionScreen();
    moduleSelectionScreen.setOnContinue(
        moduleType -> {
          switch (moduleType) {
            case ELEMENTARY:
              launchElementaryWorkspace(primaryStage);
              break;
            case TOTALISTIC:
              launchTotalisticWorkspace(primaryStage);
              break;
            default:
              throw new IllegalArgumentException("Unknown module type: " + moduleType);
          }
        });

    Scene scene = new Scene(moduleSelectionScreen);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void launchWorkspace(
      Stage primaryStage,
      String title,
      int minRule,
      int maxRule,
      int defaultRule,
      boolean enableGeneration) {
    primaryStage.setTitle(title);

    DisplayPanel displayPanel = new DisplayPanel();
    ParametersPanel parametersPanel = new ParametersPanel(minRule, maxRule, defaultRule);
    CommandsPanel commandsPanel = new CommandsPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);

    if (enableGeneration) {
      new MainController(displayPanel, parametersPanel, commandsPanel, dialogService);
    }

    HBox root = new HBox(UiStyles.APP_SPACING);
    root.setPadding(new Insets(UiStyles.APP_SPACING));
    HBox.setHgrow(sidebarPanel, javafx.scene.layout.Priority.ALWAYS);
    root.getChildren().addAll(displayPanel, sidebarPanel);

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
  }

  private void launchElementaryWorkspace(Stage primaryStage) {
    launchWorkspace(primaryStage, "Cellular Automata (Elementary)", 0, 255, 30, true);
  }

  private void launchTotalisticWorkspace(Stage primaryStage) {
    launchWorkspace(primaryStage, "Cellular Automata (Totalistic)", 0, 15, 0, false);
  }
}

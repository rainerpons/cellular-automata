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

  private void launchElementaryWorkspace(Stage primaryStage) {
    DisplayPanel displayPanel = new DisplayPanel();
    ParametersPanel parametersPanel = new ParametersPanel();
    CommandsPanel commandsPanel = new CommandsPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);

    new MainController(displayPanel, parametersPanel, commandsPanel, dialogService);

    HBox root = new HBox(UiStyles.APP_SPACING);
    root.setPadding(new Insets(UiStyles.APP_SPACING));
    HBox.setHgrow(sidebarPanel, javafx.scene.layout.Priority.ALWAYS);
    root.getChildren().addAll(displayPanel, sidebarPanel);

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
  }

  private void launchTotalisticWorkspace(Stage primaryStage) {
    DisplayPanel displayPanel = new DisplayPanel();
    ParametersPanel parametersPanel = new ParametersPanel();
    CommandsPanel commandsPanel = new CommandsPanel();
    SidebarPanel sidebarPanel = new SidebarPanel(parametersPanel, commandsPanel);

    // Totalistic generation behavior is intentionally out of scope for this issue.

    HBox root = new HBox(UiStyles.APP_SPACING);
    root.setPadding(new Insets(UiStyles.APP_SPACING));
    HBox.setHgrow(sidebarPanel, javafx.scene.layout.Priority.ALWAYS);
    root.getChildren().addAll(displayPanel, sidebarPanel);

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
  }
}

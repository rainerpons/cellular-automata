package com.cellularautomata.ui;

import com.cellularautomata.ui.screens.ElementaryWorkspace;
import com.cellularautomata.ui.screens.LifeLikeWorkspace;
import com.cellularautomata.ui.screens.ModuleSelectionScreen;
import com.cellularautomata.ui.screens.TotalisticWorkspace;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import com.cellularautomata.ui.shared.UiFonts;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Application composition root and navigation layer that routes between module selection and
 * dedicated workspace screens.
 */
public final class MainApp extends Application {

  private final DialogService dialogService;
  private final AutomatonImageService imageService;

  /** Constructs the main application with default services. */
  public MainApp() {
    this(new DialogService());
  }

  /**
   * Constructs the main application with an injected dialog service and default image service.
   *
   * @param dialogService dialog service used by workspace controllers
   * @throws IllegalArgumentException if dialogService is null
   */
  MainApp(DialogService dialogService) {
    this(dialogService, new AutomatonImageService(dialogService));
  }

  /**
   * Constructs the main application with injected dialog and image services.
   *
   * @param dialogService dialog service used by workspace controllers
   * @param imageService image service used by workspace controllers
   * @throws IllegalArgumentException if dialogService or imageService is null
   */
  MainApp(DialogService dialogService, AutomatonImageService imageService) {
    if (dialogService == null) {
      throw new IllegalArgumentException("DialogService cannot be null.");
    }
    if (imageService == null) {
      throw new IllegalArgumentException("AutomatonImageService cannot be null.");
    }
    this.dialogService = dialogService;
    this.imageService = imageService;
  }

  @Override
  public void start(Stage primaryStage) {
    UiFonts.registerBundledFonts();

    primaryStage.setResizable(false);

    showModuleSelectionScreen(primaryStage);

    primaryStage.show();
  }

  private void showModuleSelectionScreen(Stage primaryStage) {
    primaryStage.setTitle("Cellular Automata");

    ModuleSelectionScreen moduleSelectionScreen = new ModuleSelectionScreen();
    moduleSelectionScreen.setOnContinue(config -> launchWorkspace(primaryStage, config));

    Scene scene = new Scene(moduleSelectionScreen);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    primaryStage.setScene(scene);
  }

  private void launchWorkspace(Stage primaryStage, WorkspaceConfig config) {
    primaryStage.setTitle(config.getWindowTitle());

    HBox root =
        switch (config) {
          case ELEMENTARY ->
              new ElementaryWorkspace(
                  dialogService, imageService, () -> showModuleSelectionScreen(primaryStage));
          case TOTALISTIC ->
              new TotalisticWorkspace(
                  dialogService, imageService, () -> showModuleSelectionScreen(primaryStage));
          case LIFE_LIKE ->
              new LifeLikeWorkspace(imageService, () -> showModuleSelectionScreen(primaryStage));
        };

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
  }
}

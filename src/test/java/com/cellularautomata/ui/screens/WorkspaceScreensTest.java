package com.cellularautomata.ui.screens;

import com.cellularautomata.ui.JavaFxTestHelper;
import com.cellularautomata.ui.WorkspaceConfig;
import com.cellularautomata.ui.components.ModuleCard;
import com.cellularautomata.ui.services.AutomatonImageService;
import com.cellularautomata.ui.services.DialogService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorkspaceScreensTest {

  @BeforeClass
  public static void initJavaFx() {
    JavaFxTestHelper.ensureToolkitInitialized();
  }

  @Test
  public void testModuleSelectionScreenInteractionsAndNavigationCallback() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          ModuleSelectionScreen screen = new ModuleSelectionScreen();
          Button continueBtn = (Button) screen.lookup(".continue-button");
          Assert.assertNotNull(continueBtn);
          Assert.assertTrue(continueBtn.isDisabled());

          AtomicReference<WorkspaceConfig> chosenConfig = new AtomicReference<>();
          screen.setOnContinue(chosenConfig::set);

          // Firing continue without selection does not invoke callback
          continueBtn.fire();
          Assert.assertNull(chosenConfig.get());

          HBox cardsRow = (HBox) screen.getChildren().get(1);
          Assert.assertEquals(3, cardsRow.getChildren().size());
          ModuleCard elementaryCard = (ModuleCard) cardsRow.getChildren().get(0);
          ModuleCard totalisticCard = (ModuleCard) cardsRow.getChildren().get(1);
          ModuleCard lifeLikeCard = (ModuleCard) cardsRow.getChildren().get(2);

          Assert.assertEquals(WorkspaceConfig.ELEMENTARY, elementaryCard.getConfig());
          Assert.assertEquals(WorkspaceConfig.TOTALISTIC, totalisticCard.getConfig());
          Assert.assertEquals(WorkspaceConfig.LIFE_LIKE, lifeLikeCard.getConfig());

          // Select Elementary card
          elementaryCard.getOnMouseClicked().handle(null);
          Assert.assertFalse(continueBtn.isDisabled());
          Assert.assertTrue(elementaryCard.getStyleClass().contains("module-card-selected"));
          continueBtn.fire();
          Assert.assertEquals(WorkspaceConfig.ELEMENTARY, chosenConfig.get());

          // Switch selection to Totalistic card
          totalisticCard.getOnMouseClicked().handle(null);
          Assert.assertFalse(elementaryCard.getStyleClass().contains("module-card-selected"));
          Assert.assertTrue(totalisticCard.getStyleClass().contains("module-card-selected"));
          continueBtn.fire();
          Assert.assertEquals(WorkspaceConfig.TOTALISTIC, chosenConfig.get());

          // Switch selection to Life-Like card
          lifeLikeCard.getOnMouseClicked().handle(null);
          Assert.assertFalse(totalisticCard.getStyleClass().contains("module-card-selected"));
          Assert.assertTrue(lifeLikeCard.getStyleClass().contains("module-card-selected"));
          continueBtn.fire();
          Assert.assertEquals(WorkspaceConfig.LIFE_LIKE, chosenConfig.get());

          // Setting onContinue to null safely handles clicks without throwing
          screen.setOnContinue(null);
          continueBtn.fire();
        });
  }

  @Test
  public void testElementaryWorkspaceConstructorsValidationAndGetters() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);

          // 1-argument constructor
          assertThrows(
              IllegalArgumentException.class, () -> new ElementaryWorkspace((Runnable) null));
          ElementaryWorkspace ws1 = new ElementaryWorkspace(() -> {});
          Assert.assertNotNull(ws1.getController());
          Assert.assertNotNull(ws1.getParametersPanel());
          Assert.assertNotNull(ws1.getCommandsPanel());
          Assert.assertNotNull(ws1.getDisplayPanel());

          // 2-argument constructor
          assertThrows(
              IllegalArgumentException.class,
              () -> new ElementaryWorkspace((DialogService) null, () -> {}));
          assertThrows(
              IllegalArgumentException.class, () -> new ElementaryWorkspace(dialogService, null));
          ElementaryWorkspace ws2 = new ElementaryWorkspace(dialogService, () -> {});
          Assert.assertNotNull(ws2.getController());
          Assert.assertNotNull(ws2.getParametersPanel());
          Assert.assertNotNull(ws2.getCommandsPanel());
          Assert.assertNotNull(ws2.getDisplayPanel());

          // 3-argument constructor
          assertThrows(
              IllegalArgumentException.class,
              () -> new ElementaryWorkspace(null, imageService, () -> {}));
          assertThrows(
              IllegalArgumentException.class,
              () -> new ElementaryWorkspace(dialogService, null, () -> {}));
          assertThrows(
              IllegalArgumentException.class,
              () -> new ElementaryWorkspace(dialogService, imageService, null));

          ElementaryWorkspace ws3 = new ElementaryWorkspace(dialogService, imageService, () -> {});
          Assert.assertNotNull(ws3.getController());
          Assert.assertNotNull(ws3.getParametersPanel());
          Assert.assertNotNull(ws3.getCommandsPanel());
          Assert.assertNotNull(ws3.getDisplayPanel());
        });
  }

  @Test
  public void testElementaryWorkspaceBackLinkExecutesNavigationCallback() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          AtomicBoolean navigated = new AtomicBoolean(false);
          ElementaryWorkspace workspace = new ElementaryWorkspace(() -> navigated.set(true));

          Label backLink = (Label) workspace.lookup(".navigation-link");
          Assert.assertNotNull(backLink);
          Assert.assertEquals("← Back to Modules", backLink.getText());

          backLink.getOnMouseClicked().handle(null);
          Assert.assertTrue(navigated.get());
        });
  }

  @Test
  public void testTotalisticWorkspaceConstructorsValidationAndGetters() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);

          // 1-argument constructor
          assertThrows(
              IllegalArgumentException.class, () -> new TotalisticWorkspace((Runnable) null));
          TotalisticWorkspace ws1 = new TotalisticWorkspace(() -> {});
          Assert.assertNotNull(ws1.getController());
          Assert.assertNotNull(ws1.getParametersPanel());
          Assert.assertNotNull(ws1.getCommandsPanel());
          Assert.assertNotNull(ws1.getDisplayPanel());

          // 2-argument constructor
          assertThrows(
              IllegalArgumentException.class,
              () -> new TotalisticWorkspace((DialogService) null, () -> {}));
          assertThrows(
              IllegalArgumentException.class, () -> new TotalisticWorkspace(dialogService, null));
          TotalisticWorkspace ws2 = new TotalisticWorkspace(dialogService, () -> {});
          Assert.assertNotNull(ws2.getController());
          Assert.assertNotNull(ws2.getParametersPanel());
          Assert.assertNotNull(ws2.getCommandsPanel());
          Assert.assertNotNull(ws2.getDisplayPanel());

          // 3-argument constructor
          assertThrows(
              IllegalArgumentException.class,
              () -> new TotalisticWorkspace(null, imageService, () -> {}));
          assertThrows(
              IllegalArgumentException.class,
              () -> new TotalisticWorkspace(dialogService, null, () -> {}));
          assertThrows(
              IllegalArgumentException.class,
              () -> new TotalisticWorkspace(dialogService, imageService, null));

          TotalisticWorkspace ws3 = new TotalisticWorkspace(dialogService, imageService, () -> {});
          Assert.assertNotNull(ws3.getController());
          Assert.assertNotNull(ws3.getParametersPanel());
          Assert.assertNotNull(ws3.getCommandsPanel());
          Assert.assertNotNull(ws3.getDisplayPanel());
        });
  }

  @Test
  public void testTotalisticWorkspaceBackLinkExecutesNavigationCallback() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          AtomicBoolean navigated = new AtomicBoolean(false);
          TotalisticWorkspace workspace = new TotalisticWorkspace(() -> navigated.set(true));

          Label backLink = (Label) workspace.lookup(".navigation-link");
          Assert.assertNotNull(backLink);
          Assert.assertEquals("← Back to Modules", backLink.getText());

          backLink.getOnMouseClicked().handle(null);
          Assert.assertTrue(navigated.get());
        });
  }

  @Test
  public void testLifeLikeWorkspaceConstructorsValidationAndGetters() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);

          // 1-argument constructor
          assertThrows(
              IllegalArgumentException.class, () -> new LifeLikeWorkspace((Runnable) null));
          LifeLikeWorkspace ws1 = new LifeLikeWorkspace(() -> {});
          Assert.assertNotNull(ws1.getController());
          Assert.assertNotNull(ws1.getParametersPanel());
          Assert.assertNotNull(ws1.getDisplayPanel());
          Assert.assertNotNull(ws1.getToolbar());

          // 2-argument constructor with DialogService
          assertThrows(
              IllegalArgumentException.class,
              () -> new LifeLikeWorkspace((DialogService) null, () -> {}));
          assertThrows(
              IllegalArgumentException.class, () -> new LifeLikeWorkspace(dialogService, null));
          LifeLikeWorkspace ws2 = new LifeLikeWorkspace(dialogService, () -> {});
          Assert.assertNotNull(ws2.getController());
          Assert.assertNotNull(ws2.getParametersPanel());
          Assert.assertNotNull(ws2.getDisplayPanel());
          Assert.assertNotNull(ws2.getToolbar());

          // 3-argument constructor
          assertThrows(
              IllegalArgumentException.class,
              () -> new LifeLikeWorkspace((DialogService) null, null, () -> {}));
          assertThrows(
              IllegalArgumentException.class,
              () -> new LifeLikeWorkspace(dialogService, imageService, null));

          LifeLikeWorkspace ws3a = new LifeLikeWorkspace(dialogService, null, () -> {});
          Assert.assertNotNull(ws3a.getController());
          Assert.assertNotNull(ws3a.getParametersPanel());
          Assert.assertNotNull(ws3a.getDisplayPanel());
          Assert.assertNotNull(ws3a.getToolbar());

          LifeLikeWorkspace ws3b = new LifeLikeWorkspace(null, imageService, () -> {});
          Assert.assertNotNull(ws3b.getController());
          Assert.assertNotNull(ws3b.getParametersPanel());
          Assert.assertNotNull(ws3b.getDisplayPanel());
          Assert.assertNotNull(ws3b.getToolbar());

          // 2-argument constructor with AutomatonImageService
          assertThrows(
              IllegalArgumentException.class,
              () -> new LifeLikeWorkspace((AutomatonImageService) null, () -> {}));
          assertThrows(
              IllegalArgumentException.class, () -> new LifeLikeWorkspace(imageService, null));
          LifeLikeWorkspace ws4 = new LifeLikeWorkspace(imageService, () -> {});
          Assert.assertNotNull(ws4.getController());
          Assert.assertNotNull(ws4.getParametersPanel());
          Assert.assertNotNull(ws4.getDisplayPanel());
          Assert.assertNotNull(ws4.getToolbar());
        });
  }

  @Test
  public void testLifeLikeWorkspaceDirectDisposeReleasesResources() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);
          LifeLikeWorkspace workspace = new LifeLikeWorkspace(imageService, () -> {});

          Button stepButton = (Button) workspace.getToolbar().getChildren().get(1);
          Button resetButton = (Button) workspace.getToolbar().getChildren().get(2);
          Button saveButton = (Button) workspace.getToolbar().getChildren().get(3);

          // Advance one step so simulation is active and Reset/Save become enabled
          stepButton.fire();
          Assert.assertNotNull(workspace.getDisplayPanel().getAutomatonImage());
          Assert.assertFalse(resetButton.isDisabled());
          Assert.assertFalse(saveButton.isDisabled());

          // Calling dispose directly must reset simulation state and disable Reset and Save
          workspace.dispose();
          Assert.assertFalse(workspace.getToolbar().isPlaying());
          Assert.assertTrue(resetButton.isDisabled());
          Assert.assertTrue(saveButton.isDisabled());
        });
  }

  @Test
  public void testLifeLikeWorkspaceBackLinkTriggersDisposeAndNavigatesBack() {
    JavaFxTestHelper.runOnFxThread(
        () -> {
          AtomicBoolean navigated = new AtomicBoolean(false);
          DialogService dialogService = new DialogService();
          AutomatonImageService imageService = new AutomatonImageService(dialogService);
          LifeLikeWorkspace workspace =
              new LifeLikeWorkspace(imageService, () -> navigated.set(true));

          Button playPauseButton = (Button) workspace.getToolbar().getChildren().get(0);
          Button resetButton = (Button) workspace.getToolbar().getChildren().get(2);
          Button saveButton = (Button) workspace.getToolbar().getChildren().get(3);

          // Start simulation playback
          playPauseButton.fire();
          Assert.assertTrue(workspace.getToolbar().isPlaying());
          Assert.assertFalse(resetButton.isDisabled());

          Label backLink = (Label) workspace.lookup(".navigation-link");
          Assert.assertNotNull(backLink);
          Assert.assertEquals("← Back to Modules", backLink.getText());

          // Clicking the back-link must invoke dispose() (stopping playback and clearing state)
          // as well as running the navigation callback
          backLink.getOnMouseClicked().handle(null);
          Assert.assertTrue(navigated.get());
          Assert.assertFalse(workspace.getToolbar().isPlaying());
          Assert.assertTrue(resetButton.isDisabled());
          Assert.assertTrue(saveButton.isDisabled());
        });
  }

  private static <T extends Throwable> void assertThrows(
      Class<T> expectedType, Runnable executable) {
    try {
      executable.run();
    } catch (Throwable actual) {
      if (expectedType.isInstance(actual)) {
        return;
      }
      throw new AssertionError(
          "Expected "
              + expectedType.getName()
              + " to be thrown, but "
              + actual.getClass().getName()
              + " was thrown instead",
          actual);
    }
    throw new AssertionError(
        "Expected " + expectedType.getName() + " to be thrown, but nothing was thrown");
  }
}

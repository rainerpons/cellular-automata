package com.cellularautomata.ui.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.stage.Window;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomatonImageService}. */
public class AutomatonImageServiceTest {

  @Test
  public void testConstructorValidation() {
    DialogService dialogService = new DialogService();
    AutomatonImageService.ImageWriter writer = (img, dest) -> {};

    try {
      new AutomatonImageService((DialogService) null);
      Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      Assert.assertEquals("DialogService cannot be null.", expected.getMessage());
    }

    try {
      new AutomatonImageService(null, writer);
      Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      Assert.assertEquals("DialogService cannot be null.", expected.getMessage());
    }

    try {
      new AutomatonImageService(dialogService, null);
      Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      Assert.assertEquals("ImageWriter cannot be null.", expected.getMessage());
    }

    AutomatonImageService defaultService = new AutomatonImageService();
    Assert.assertNotNull(defaultService);

    AutomatonImageService serviceWithDialog = new AutomatonImageService(dialogService);
    Assert.assertNotNull(serviceWithDialog);
  }

  @Test
  public void testResolveInitialSaveDirectoryDesktopExists() throws IOException {
    Path tempHome = Files.createTempDirectory("test-home-desktop-");
    try {
      Path desktopDir = Files.createDirectory(tempHome.resolve("Desktop"));
      File resolved = AutomatonImageService.resolveInitialSaveDirectory(tempHome.toString());

      Assert.assertNotNull(resolved);
      Assert.assertEquals(desktopDir.toFile(), resolved);
    } finally {
      deleteRecursively(tempHome);
    }
  }

  @Test
  public void testResolveInitialSaveDirectoryDesktopDoesNotExistFallsBackToHome()
      throws IOException {
    Path tempHome = Files.createTempDirectory("test-home-nodesktop-");
    try {
      File resolved = AutomatonImageService.resolveInitialSaveDirectory(tempHome.toString());

      Assert.assertNotNull(resolved);
      Assert.assertEquals(tempHome.toFile(), resolved);
    } finally {
      deleteRecursively(tempHome);
    }
  }

  @Test
  public void testResolveInitialSaveDirectoryNeitherExistsReturnsNull() throws IOException {
    Path tempDir = Files.createTempDirectory("test-nonexistent-base-");
    Path nonexistentHome = tempDir.resolve("does-not-exist");
    try {
      File resolved = AutomatonImageService.resolveInitialSaveDirectory(nonexistentHome.toString());
      Assert.assertNull(resolved);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  public void testResolveInitialSaveDirectoryNullOrBlankReturnsNull() {
    Assert.assertNull(AutomatonImageService.resolveInitialSaveDirectory(null));
    Assert.assertNull(AutomatonImageService.resolveInitialSaveDirectory(""));
    Assert.assertNull(AutomatonImageService.resolveInitialSaveDirectory("   "));
  }

  @Test
  public void testResolveInitialSaveDirectoryZeroArgDelegatesToUserHome() {
    AutomatonImageService service = new AutomatonImageService();
    File resolved = service.resolveInitialSaveDirectory();
    String userHome = System.getProperty("user.home");
    File expected = AutomatonImageService.resolveInitialSaveDirectory(userHome);
    Assert.assertEquals(expected, resolved);
  }

  @Test
  public void testSaveImageInputValidation() {
    AutomatonImageService service = new AutomatonImageService();
    BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

    try {
      service.saveImage(null, null, "filename.png");
      Assert.fail("Expected IllegalArgumentException for null image");
    } catch (IllegalArgumentException expected) {
      Assert.assertEquals("Image cannot be null.", expected.getMessage());
    }

    try {
      service.saveImage(null, image, null);
      Assert.fail("Expected IllegalArgumentException for null suggestedFileName");
    } catch (IllegalArgumentException expected) {
      Assert.assertEquals("Suggested file name cannot be null or blank.", expected.getMessage());
    }

    try {
      service.saveImage(null, image, "");
      Assert.fail("Expected IllegalArgumentException for empty suggestedFileName");
    } catch (IllegalArgumentException expected) {
      Assert.assertEquals("Suggested file name cannot be null or blank.", expected.getMessage());
    }

    try {
      service.saveImage(null, image, "   ");
      Assert.fail("Expected IllegalArgumentException for blank suggestedFileName");
    } catch (IllegalArgumentException expected) {
      Assert.assertEquals("Suggested file name cannot be null or blank.", expected.getMessage());
    }
  }

  @Test
  public void testSaveImageSuccessWorkflow() {
    RecordingDialogService dialogService = new RecordingDialogService();
    File targetFile = new File("test-export.png");
    dialogService.fileToReturn = targetFile;

    AtomicReference<BufferedImage> writtenImage = new AtomicReference<>();
    AtomicReference<File> writtenDestination = new AtomicReference<>();

    AutomatonImageService service =
        new AutomatonImageService(
            dialogService,
            (img, dest) -> {
              writtenImage.set(img);
              writtenDestination.set(dest);
            });

    BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
    boolean result = service.saveImage(null, image, "suggested.png");

    Assert.assertTrue("saveImage should return true on success", result);
    Assert.assertEquals(1, dialogService.saveDialogCalls);
    Assert.assertEquals("suggested.png", dialogService.lastSuggestedFileName);
    Assert.assertFalse("showSaveError should not be called", dialogService.saveErrorShown);
    Assert.assertSame(image, writtenImage.get());
    Assert.assertEquals(targetFile, writtenDestination.get());
  }

  @Test
  public void testSaveImageCancellationWorkflow() {
    RecordingDialogService dialogService = new RecordingDialogService();
    dialogService.fileToReturn = null; // User cancelled

    AtomicBoolean writerInvoked = new AtomicBoolean(false);

    AutomatonImageService service =
        new AutomatonImageService(
            dialogService,
            (img, dest) -> {
              writerInvoked.set(true);
            });

    BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
    boolean result = service.saveImage(null, image, "suggested.png");

    Assert.assertFalse("saveImage should return false on cancellation", result);
    Assert.assertEquals(1, dialogService.saveDialogCalls);
    Assert.assertFalse("writer should not be invoked when cancelled", writerInvoked.get());
    Assert.assertFalse(
        "showSaveError should not be called on cancel", dialogService.saveErrorShown);
  }

  @Test
  public void testSaveImageFailureWorkflowCallsShowSaveError() {
    RecordingDialogService dialogService = new RecordingDialogService();
    File targetFile = new File("test-fail.png");
    dialogService.fileToReturn = targetFile;

    AutomatonImageService service =
        new AutomatonImageService(
            dialogService,
            (img, dest) -> {
              throw new IOException("Simulated disk error");
            });

    BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
    boolean result = service.saveImage(null, image, "suggested.png");

    Assert.assertFalse("saveImage should return false on IOException", result);
    Assert.assertEquals(1, dialogService.saveDialogCalls);
    Assert.assertTrue("showSaveError must be called on failure", dialogService.saveErrorShown);
    Assert.assertEquals(1, dialogService.saveErrorCalls);
  }

  private static void deleteRecursively(Path root) throws IOException {
    if (!Files.exists(root)) {
      return;
    }
    try (var stream = Files.walk(root)) {
      stream
          .sorted((a, b) -> b.compareTo(a))
          .forEach(
              path -> {
                try {
                  Files.deleteIfExists(path);
                } catch (IOException e) {
                  // ignored during cleanup
                }
              });
    }
  }

  private static final class RecordingDialogService extends DialogService {
    private File fileToReturn;
    private int saveDialogCalls;
    private int saveErrorCalls;
    private boolean saveErrorShown;
    private String lastSuggestedFileName;

    @Override
    public File showSaveFileDialog(Window owner, File initialDirectory, String initialFileName) {
      this.saveDialogCalls++;
      this.lastSuggestedFileName = initialFileName;
      return fileToReturn;
    }

    @Override
    public void showSaveError() {
      this.saveErrorCalls++;
      this.saveErrorShown = true;
    }
  }
}

package com.cellularautomata.image;

import com.cellularautomata.config.WorkspaceConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomatonImageExport}. */
public class AutomatonImageExportTest {

  @Test
  public void testGenerateFileNameElementary() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    String fileName =
        AutomatonImageExport.generateFileName(WorkspaceConfig.ELEMENTARY, 110, 64, 2, time);

    Assert.assertEquals("elementary_rule110_size64_2026-07-13T15-30-45.png", fileName);
  }

  @Test
  public void testGenerateFileNameTotalistic() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    String fileName =
        AutomatonImageExport.generateFileName(WorkspaceConfig.TOTALISTIC, 777, 128, 3, time);

    Assert.assertEquals("totalistic_states3_rule777_size128_2026-07-13T15-30-45.png", fileName);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGenerateFileNameLifeLikeThrows() {
    LocalDateTime time = LocalDateTime.of(2026, 7, 13, 15, 30, 45);
    AutomatonImageExport.generateFileName(WorkspaceConfig.LIFE_LIKE, 0, 64, 2, time);
  }

  @Test
  public void testResolveInitialSaveDirectoryDesktopExists() throws IOException {
    Path tempHome = Files.createTempDirectory("test-home-desktop-");
    try {
      Path desktopDir = Files.createDirectory(tempHome.resolve("Desktop"));
      File resolved = AutomatonImageExport.resolveInitialSaveDirectory(tempHome.toString());

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
      File resolved = AutomatonImageExport.resolveInitialSaveDirectory(tempHome.toString());

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
      File resolved = AutomatonImageExport.resolveInitialSaveDirectory(nonexistentHome.toString());
      Assert.assertNull(resolved);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  public void testResolveInitialSaveDirectoryNullOrBlankReturnsNull() {
    Assert.assertNull(AutomatonImageExport.resolveInitialSaveDirectory(null));
    Assert.assertNull(AutomatonImageExport.resolveInitialSaveDirectory(""));
    Assert.assertNull(AutomatonImageExport.resolveInitialSaveDirectory("   "));
  }

  @Test
  public void testResolveInitialSaveDirectoryNoArgDelegatesToSystemHome() {
    File resolved = AutomatonImageExport.resolveInitialSaveDirectory();
    String userHome = System.getProperty("user.home");
    if (userHome != null && !userHome.isBlank()) {
      File desktop = new File(userHome, "Desktop");
      File home = new File(userHome);
      if (desktop.exists() && desktop.isDirectory()) {
        Assert.assertEquals(desktop, resolved);
      } else if (home.exists() && home.isDirectory()) {
        Assert.assertEquals(home, resolved);
      } else {
        Assert.assertNull(resolved);
      }
    } else {
      Assert.assertNull(resolved);
    }
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
}

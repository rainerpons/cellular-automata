package com.cellularautomata.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AutomatonImageExport}. */
public class AutomatonImageExportTest {

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

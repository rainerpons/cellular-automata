package com.cellularautomata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;

/**
 * Verifies the dependency boundaries documented in {@code docs/ARCHITECTURE.md}: {@code UI ->
 * Config <- Engine}, with {@code Config} independent of {@code Engine} and {@code UI}, and {@code
 * Engine} independent of {@code UI}.
 */
public class ArchitectureBoundaryTest {
  private static final Path MAIN_JAVA_ROOT = Path.of("src/main/java/com/cellularautomata");

  @Test
  public void testConfigPackageDoesNotDependOnEngineUiImageOrJavaFx() throws IOException {
    assertNoForbiddenImports(
        MAIN_JAVA_ROOT.resolve("config"),
        List.of(
            "com.cellularautomata.engine",
            "com.cellularautomata.ui",
            "com.cellularautomata.image",
            "com.cellularautomata.app",
            "javafx."));
  }

  @Test
  public void testEnginePackageDoesNotDependOnUiImageOrJavaFx() throws IOException {
    assertNoForbiddenImports(
        MAIN_JAVA_ROOT.resolve("engine"),
        List.of(
            "com.cellularautomata.ui",
            "com.cellularautomata.image",
            "com.cellularautomata.app",
            "javafx."));
  }

  @Test
  public void testLifeLikeRuleSelectionModelIsIndependentOfJavaFx() throws IOException {
    Path modelFile = MAIN_JAVA_ROOT.resolve("ui/panels/LifeLikeRuleSelectionModel.java");
    Assert.assertTrue("Expected LifeLikeRuleSelectionModel.java to exist", Files.exists(modelFile));
    assertFileHasNoForbiddenImports(modelFile, List.of("javafx."));
  }

  private static void assertNoForbiddenImports(Path packageDir, List<String> forbiddenPrefixes)
      throws IOException {
    Assert.assertTrue(
        "Expected package directory to exist: " + packageDir, Files.isDirectory(packageDir));
    try (Stream<Path> paths = Files.walk(packageDir)) {
      List<Path> javaFiles = paths.filter(p -> p.toString().endsWith(".java")).toList();
      Assert.assertFalse(
          "Expected at least one Java source file in " + packageDir, javaFiles.isEmpty());
      for (Path file : javaFiles) {
        assertFileHasNoForbiddenImports(file, forbiddenPrefixes);
      }
    }
  }

  private static void assertFileHasNoForbiddenImports(Path file, List<String> forbiddenPrefixes)
      throws IOException {
    List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
    for (String line : lines) {
      String trimmed = line.trim();
      if (!trimmed.startsWith("import ")) {
        continue;
      }
      for (String forbidden : forbiddenPrefixes) {
        Assert.assertFalse(
            "Forbidden import in " + file + ": " + trimmed, trimmed.contains(forbidden));
      }
    }
  }
}

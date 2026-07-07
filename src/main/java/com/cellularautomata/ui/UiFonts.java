package com.cellularautomata.ui;

import java.io.InputStream;
import javafx.scene.text.Font;

/** Loads and registers the bundled IBM Plex Sans font at application startup. */
final class UiFonts {
  static final String IBM_PLEX_SANS = "IBM Plex Sans";

  private UiFonts() {}

  /** Attempts to register IBM Plex Sans. Failures are logged to stderr and do not propagate. */
  static void registerBundledFonts() {
    register("/fonts/IBMPlexSans-Regular.ttf");
    register("/fonts/IBMPlexSans-Bold.ttf");
  }

  private static void register(String resourcePath) {
    try (InputStream stream = UiFonts.class.getResourceAsStream(resourcePath)) {
      if (stream == null) {
        System.err.println("Bundled font not found: " + resourcePath);
        return;
      }
      Font.loadFont(stream, 13);
    } catch (java.io.IOException e) {
      System.err.println("Could not load bundled font: " + resourcePath);
    }
  }
}

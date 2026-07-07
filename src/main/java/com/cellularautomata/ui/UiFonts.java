package com.cellularautomata.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

/**
 * Loads and registers the bundled IBM Plex Sans font at application startup. Falls back silently to
 * the FlatLaf default font if the resource cannot be loaded.
 */
final class UiFonts {
  static final String IBM_PLEX_SANS = "IBM Plex Sans";

  private UiFonts() {}

  /**
   * Attempts to register IBM Plex Sans with the local {@link GraphicsEnvironment}. Safe to call
   * before any Swing components are created. Failures are logged to stderr and do not propagate.
   */
  static void registerBundledFonts() {
    register("/fonts/IBMPlexSans-Regular.ttf");
    register("/fonts/IBMPlexSans-Bold.ttf");
  }

  private static void register(String resourcePath) {
    try (var stream = UiFonts.class.getResourceAsStream(resourcePath)) {
      if (stream == null) {
        System.err.println("Bundled font not found: " + resourcePath);
        return;
      }
      var font = Font.createFont(Font.TRUETYPE_FONT, stream);
      GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
    } catch (FontFormatException | IOException e) {
      System.err.println("Could not load bundled font: " + resourcePath);
    }
  }
}

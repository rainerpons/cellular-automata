package com.cellularautomata.app;

import com.cellularautomata.ui.MainApp;
import javafx.application.Application;

/**
 * The <code>Main</code> class is the entry point for the application.
 *
 * @author Rainer Pons
 */
public class Main {
  // Retain a strong reference to the logger to prevent it from being garbage collected
  private static final java.util.logging.Logger PLATFORM_LOGGER =
      java.util.logging.Logger.getLogger("com.sun.javafx.application.PlatformImpl");

  /**
   * Main method to run the application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    PLATFORM_LOGGER.setLevel(java.util.logging.Level.SEVERE);

    java.io.PrintStream originalErr = System.err;
    System.setErr(
        new java.io.PrintStream(originalErr) {
          @Override
          public void print(String s) {
            if (s != null && s.contains("WARNING: Unsupported JavaFX configuration")) {
              return;
            }
            super.print(s);
          }

          @Override
          public void println(String x) {
            if (x != null && x.contains("WARNING: Unsupported JavaFX configuration")) {
              return;
            }
            super.println(x);
          }

          @Override
          public void write(byte[] buf, int off, int len) {
            String s = new String(buf, off, len);
            if (s.contains("WARNING: Unsupported JavaFX configuration")) {
              return;
            }
            super.write(buf, off, len);
          }
        });
    Application.launch(MainApp.class, args);
  }
}

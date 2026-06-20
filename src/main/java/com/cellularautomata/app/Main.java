package com.cellularautomata.app;

import com.cellularautomata.ui.MainFrame;
import java.awt.EventQueue;

/**
 * The <code>Main</code> class is the entry point for the application.
 *
 * @author Rainer Pons
 */
public class Main {
  /**
   * Main method to run the application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(
        new Runnable() {
          public void run() {
            try {
              MainFrame window = new MainFrame();
              window.setVisible(true);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
  }
}

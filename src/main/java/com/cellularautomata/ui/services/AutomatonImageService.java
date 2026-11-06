package com.cellularautomata.ui.services;

import com.cellularautomata.image.AutomatonImageUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.stage.Window;

/**
 * Service coordinating the export of rendered cellular automata images to disk, including initial
 * save directory resolution, destination file selection via {@link DialogService}, and PNG writing.
 */
@SuppressFBWarnings(
    value = "CT_CONSTRUCTOR_THROW",
    justification = "Constructor arguments must be validated to ensure non-null collaborators")
public class AutomatonImageService {

  /** Functional writer interface abstracting image file writing to disk. */
  @FunctionalInterface
  interface ImageWriter {

    /**
     * Writes the given buffered image to the destination file.
     *
     * @param image the image to write
     * @param destination the target file
     * @throws IOException if an error occurs during writing
     */
    void write(BufferedImage image, File destination) throws IOException;
  }

  private final DialogService dialogService;
  private final ImageWriter imageWriter;

  /** Constructs the image service with default dialog service and PNG writer. */
  public AutomatonImageService() {
    this(new DialogService());
  }

  /**
   * Constructs the image service with an injected dialog service and default PNG writer.
   *
   * @param dialogService the dialog service used for user interaction
   */
  public AutomatonImageService(DialogService dialogService) {
    this(dialogService, AutomatonImageUtils::saveImage);
  }

  /**
   * Constructs the image service with injected dialog service and image writer for testing.
   *
   * @param dialogService the dialog service used for user interaction
   * @param imageWriter the image writer
   */
  AutomatonImageService(DialogService dialogService, ImageWriter imageWriter) {
    if (dialogService == null) {
      throw new IllegalArgumentException("DialogService cannot be null.");
    }
    if (imageWriter == null) {
      throw new IllegalArgumentException("ImageWriter cannot be null.");
    }
    this.dialogService = dialogService;
    this.imageWriter = imageWriter;
  }

  /**
   * Prompts the user to choose a destination file and writes the automaton image.
   *
   * @param owner the owner window for the file chooser dialog
   * @param image the rendered automaton image to save
   * @param suggestedFileName default file name populated in the save dialog
   * @return {@code true} if the image was successfully saved, {@code false} if cancelled or failed
   * @throws IllegalArgumentException if image is null or suggestedFileName is null/blank
   */
  public boolean saveImage(Window owner, BufferedImage image, String suggestedFileName) {
    if (image == null) {
      throw new IllegalArgumentException("Image cannot be null.");
    }
    if (suggestedFileName == null || suggestedFileName.isBlank()) {
      throw new IllegalArgumentException("Suggested file name cannot be null or blank.");
    }

    File initialDirectory = resolveInitialSaveDirectory();
    File destination = dialogService.showSaveFileDialog(owner, initialDirectory, suggestedFileName);
    if (destination == null) {
      return false;
    }

    try {
      imageWriter.write(image, destination);
      return true;
    } catch (IOException e) {
      System.err.println("Error saving image: " + e.getMessage());
      dialogService.showSaveError();
      return false;
    }
  }

  /**
   * Resolves the preferred initial directory for saving automaton images, preferring {@code
   * ~/Desktop} and falling back to {@code user.home}.
   *
   * @return the initial save directory, or {@code null} if neither exists
   */
  public File resolveInitialSaveDirectory() {
    return resolveInitialSaveDirectory(System.getProperty("user.home"));
  }

  static File resolveInitialSaveDirectory(String userHome) {
    if (userHome == null || userHome.isBlank()) {
      return null;
    }
    File desktop = new File(userHome, "Desktop");
    if (desktop.exists() && desktop.isDirectory()) {
      return desktop;
    }
    File home = new File(userHome);
    if (home.exists() && home.isDirectory()) {
      return home;
    }
    return null;
  }
}

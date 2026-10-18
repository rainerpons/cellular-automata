package com.cellularautomata.ui;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import org.junit.Assert;

/**
 * Shared test helper for initializing the JavaFX toolkit once and executing UI assertions on the
 * JavaFX Application Thread.
 */
public final class JavaFxTestHelper {
  private static final long TIMEOUT_SECONDS = 5;

  private static boolean initialized;
  private static Throwable initializationFailure;

  private JavaFxTestHelper() {}

  /**
   * Initializes the JavaFX toolkit once if it is not already running, or rethrows the recorded
   * failure if a prior initialization attempt failed.
   */
  public static synchronized void ensureToolkitInitialized() {
    if (initialized) {
      return;
    }
    if (initializationFailure != null) {
      throw new IllegalStateException(
          "JavaFX toolkit initialization previously failed", initializationFailure);
    }

    CountDownLatch latch = new CountDownLatch(1);
    try {
      Platform.startup(latch::countDown);
      if (!latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
        initializationFailure =
            new IllegalStateException(
                "Timed out waiting for JavaFX toolkit startup after "
                    + TIMEOUT_SECONDS
                    + " seconds");
      } else {
        initialized = true;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      initializationFailure =
          new IllegalStateException("Interrupted while initializing JavaFX toolkit", e);
    } catch (Throwable t) {
      initializationFailure = t;
    }

    if (initializationFailure != null) {
      throw new IllegalStateException("Failed to initialize JavaFX toolkit", initializationFailure);
    }
  }

  /**
   * Executes the given action synchronously on the JavaFX Application Thread, propagating any
   * {@link AssertionError} or {@link RuntimeException} back to the calling test thread.
   *
   * @param action the UI test action to run on the FX thread
   */
  public static void runOnFxThread(Runnable action) {
    ensureToolkitInitialized();
    if (Platform.isFxApplicationThread()) {
      action.run();
      return;
    }

    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Platform.runLater(
        () -> {
          try {
            action.run();
          } catch (Throwable t) {
            failure.set(t);
          } finally {
            latch.countDown();
          }
        });

    try {
      Assert.assertTrue(
          "Timed out waiting for action on JavaFX Application Thread",
          latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Interrupted while waiting for JavaFX Application Thread", e);
    }

    Throwable thrown = failure.get();
    if (thrown instanceof RuntimeException runtimeException) {
      throw runtimeException;
    }
    if (thrown instanceof Error error) {
      throw error;
    }
    if (thrown != null) {
      throw new RuntimeException(thrown);
    }
  }
}

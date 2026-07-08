module com.cellularautomata {
  requires transitive javafx.controls;
  requires transitive javafx.graphics;
  requires javafx.swing;
  requires static com.github.spotbugs.annotations;

  exports com.cellularautomata.app;
  exports com.cellularautomata.ui;
  exports com.cellularautomata.engine;
}

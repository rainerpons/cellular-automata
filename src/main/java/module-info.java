module com.cellularautomata {
  requires javafx.controls;
  requires javafx.swing;
  requires static com.github.spotbugs.annotations;

  exports com.cellularautomata.app;
  exports com.cellularautomata.ui;
  exports com.cellularautomata.engine;
}

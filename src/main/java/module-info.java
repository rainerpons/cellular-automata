module com.cellularautomata {
  requires javafx.controls;
  requires transitive javafx.graphics;
  requires javafx.swing;
  requires org.kordamp.ikonli.core;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.ikonli.materialdesign2;
  requires static com.github.spotbugs.annotations;

  exports com.cellularautomata.app;
  exports com.cellularautomata.ui;
  exports com.cellularautomata.engine;
  exports com.cellularautomata.engine.automata;
  exports com.cellularautomata.engine.presets;
  exports com.cellularautomata.engine.rules;
  exports com.cellularautomata.config;
}

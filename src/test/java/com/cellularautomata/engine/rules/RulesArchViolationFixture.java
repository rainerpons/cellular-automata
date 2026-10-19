package com.cellularautomata.engine.rules;

/** Test fixture providing synthetic classes that violate engine rules architectural boundaries. */
public final class RulesArchViolationFixture {
  private RulesArchViolationFixture() {}

  public static class ViolatesRulesToPresets {
    com.cellularautomata.engine.presets.LifeLikePreset preset;
  }

  public static class ViolatesRulesToAutomata {
    com.cellularautomata.engine.automata.LifeLikeSimulation simulation;
  }

  public static class ViolatesRulesToGenerator {
    com.cellularautomata.engine.Generator generator;
  }
}

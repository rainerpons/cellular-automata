package com.cellularautomata.ui.simulation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Unit tests for {@link LifeLikeStateMachine} verifying state transitions and contracts. */
public class LifeLikeStateMachineTest {

  private LifeLikeStateMachine stateMachine;

  @Before
  public void setUp() {
    stateMachine = new LifeLikeStateMachine();
  }

  @Test
  public void testInitialStateIsUninitialized() {
    Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isUninitialized());
    Assert.assertFalse(stateMachine.isRunning());
    Assert.assertFalse(stateMachine.isPaused());
    Assert.assertFalse(stateMachine.isFixedPoint());
  }

  @Test
  public void testUninitializedToRunningOnPlay() {
    stateMachine.onPlay();
    Assert.assertEquals(LifeLikeStateMachine.State.RUNNING, stateMachine.getState());
    Assert.assertFalse(stateMachine.isUninitialized());
    Assert.assertTrue(stateMachine.isRunning());
    Assert.assertFalse(stateMachine.isPaused());
  }

  @Test
  public void testUninitializedToPausedOnStep() {
    stateMachine.onStep();
    Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, stateMachine.getState());
    Assert.assertFalse(stateMachine.isUninitialized());
    Assert.assertFalse(stateMachine.isRunning());
    Assert.assertTrue(stateMachine.isPaused());
  }

  @Test(expected = IllegalStateException.class)
  public void testUninitializedRejectsOnPause() {
    stateMachine.onPause();
  }

  @Test(expected = IllegalStateException.class)
  public void testUninitializedRejectsOnReset() {
    stateMachine.onReset();
  }

  @Test(expected = IllegalStateException.class)
  public void testUninitializedRejectsOnError() {
    stateMachine.onError();
  }

  @Test(expected = IllegalStateException.class)
  public void testUninitializedRejectsOnStop() {
    stateMachine.onStop();
  }

  @Test(expected = IllegalStateException.class)
  public void testUninitializedRejectsOnFixedPoint() {
    stateMachine.onFixedPoint();
  }

  @Test
  public void testRunningToFixedPointOnFixedPoint() {
    stateMachine.onPlay();
    stateMachine.onFixedPoint();
    Assert.assertEquals(LifeLikeStateMachine.State.FIXED_POINT, stateMachine.getState());
    Assert.assertFalse(stateMachine.isUninitialized());
    Assert.assertFalse(stateMachine.isRunning());
    Assert.assertFalse(stateMachine.isPaused());
    Assert.assertTrue(stateMachine.isFixedPoint());
  }

  @Test
  public void testPausedToFixedPointOnFixedPoint() {
    stateMachine.onStep();
    stateMachine.onFixedPoint();
    Assert.assertEquals(LifeLikeStateMachine.State.FIXED_POINT, stateMachine.getState());
    Assert.assertFalse(stateMachine.isUninitialized());
    Assert.assertFalse(stateMachine.isRunning());
    Assert.assertFalse(stateMachine.isPaused());
    Assert.assertTrue(stateMachine.isFixedPoint());
  }

  @Test
  public void testFixedPointToPausedOnReset() {
    stateMachine.onPlay();
    stateMachine.onFixedPoint();
    stateMachine.onReset();
    Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isPaused());
    Assert.assertFalse(stateMachine.isFixedPoint());
  }

  @Test
  public void testFixedPointToUninitializedOnError() {
    stateMachine.onPlay();
    stateMachine.onFixedPoint();
    stateMachine.onError();
    Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isUninitialized());
    Assert.assertFalse(stateMachine.isFixedPoint());
  }

  @Test
  public void testFixedPointToUninitializedOnStop() {
    stateMachine.onPlay();
    stateMachine.onFixedPoint();
    stateMachine.onStop();
    Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isUninitialized());
    Assert.assertFalse(stateMachine.isFixedPoint());
  }

  @Test(expected = IllegalStateException.class)
  public void testFixedPointRejectsOnPlay() {
    stateMachine.onPlay();
    stateMachine.onFixedPoint();
    stateMachine.onPlay();
  }

  @Test(expected = IllegalStateException.class)
  public void testFixedPointRejectsOnPause() {
    stateMachine.onPlay();
    stateMachine.onFixedPoint();
    stateMachine.onPause();
  }

  @Test(expected = IllegalStateException.class)
  public void testFixedPointRejectsOnStep() {
    stateMachine.onPlay();
    stateMachine.onFixedPoint();
    stateMachine.onStep();
  }

  @Test(expected = IllegalStateException.class)
  public void testFixedPointRejectsOnFixedPoint() {
    stateMachine.onPlay();
    stateMachine.onFixedPoint();
    stateMachine.onFixedPoint();
  }

  @Test
  public void testRunningToPausedOnPause() {
    stateMachine.onPlay();
    stateMachine.onPause();
    Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isPaused());
  }

  @Test
  public void testRunningToPausedOnReset() {
    stateMachine.onPlay();
    stateMachine.onReset();
    Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isPaused());
  }

  @Test
  public void testRunningToUninitializedOnError() {
    stateMachine.onPlay();
    stateMachine.onError();
    Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isUninitialized());
  }

  @Test
  public void testRunningToUninitializedOnStop() {
    stateMachine.onPlay();
    stateMachine.onStop();
    Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isUninitialized());
  }

  @Test(expected = IllegalStateException.class)
  public void testRunningRejectsOnPlay() {
    stateMachine.onPlay();
    stateMachine.onPlay();
  }

  @Test(expected = IllegalStateException.class)
  public void testRunningRejectsOnStep() {
    stateMachine.onPlay();
    stateMachine.onStep();
  }

  @Test
  public void testPausedToRunningOnPlay() {
    stateMachine.onStep();
    stateMachine.onPlay();
    Assert.assertEquals(LifeLikeStateMachine.State.RUNNING, stateMachine.getState());
    Assert.assertTrue(stateMachine.isRunning());
  }

  @Test
  public void testPausedToPausedOnStep() {
    stateMachine.onStep();
    stateMachine.onStep();
    Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isPaused());
  }

  @Test
  public void testPausedToPausedOnReset() {
    stateMachine.onStep();
    stateMachine.onReset();
    Assert.assertEquals(LifeLikeStateMachine.State.PAUSED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isPaused());
  }

  @Test
  public void testPausedToUninitializedOnError() {
    stateMachine.onStep();
    stateMachine.onError();
    Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isUninitialized());
  }

  @Test
  public void testPausedToUninitializedOnStop() {
    stateMachine.onStep();
    stateMachine.onStop();
    Assert.assertEquals(LifeLikeStateMachine.State.UNINITIALIZED, stateMachine.getState());
    Assert.assertTrue(stateMachine.isUninitialized());
  }

  @Test(expected = IllegalStateException.class)
  public void testPausedRejectsOnPause() {
    stateMachine.onStep();
    stateMachine.onPause();
  }
}

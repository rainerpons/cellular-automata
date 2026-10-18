package com.cellularautomata.engine;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class SeedTypeTest {

  @Test
  public void testIdentifiersAndDisplayNames() {
    Assert.assertEquals("density", SeedType.DENSITY.getIdentifier());
    Assert.assertEquals("Density", SeedType.DENSITY.getDisplayName());
    Assert.assertEquals("Density", SeedType.DENSITY.toString());

    Assert.assertEquals("uniform", SeedType.UNIFORM.getIdentifier());
    Assert.assertEquals("Uniform", SeedType.UNIFORM.getDisplayName());
    Assert.assertEquals("Uniform", SeedType.UNIFORM.toString());

    Assert.assertEquals("sparse", SeedType.SPARSE.getIdentifier());
    Assert.assertEquals("Sparse", SeedType.SPARSE.getDisplayName());
    Assert.assertEquals("Sparse", SeedType.SPARSE.toString());

    Assert.assertEquals("alternating", SeedType.ALTERNATING.getIdentifier());
    Assert.assertEquals("Alternating", SeedType.ALTERNATING.getDisplayName());
    Assert.assertEquals("Alternating", SeedType.ALTERNATING.toString());
  }

  @Test
  public void testFromIdentifierLookup() {
    Assert.assertEquals(SeedType.DENSITY, SeedType.fromIdentifier("density"));
    Assert.assertEquals(SeedType.DENSITY, SeedType.fromIdentifier("DENSITY"));
    Assert.assertEquals(SeedType.DENSITY, SeedType.fromIdentifier("Density"));

    Assert.assertEquals(SeedType.UNIFORM, SeedType.fromIdentifier("uniform"));
    Assert.assertEquals(SeedType.UNIFORM, SeedType.fromIdentifier("UNIFORM"));
    Assert.assertEquals(SeedType.UNIFORM, SeedType.fromIdentifier("Uniform"));

    Assert.assertEquals(SeedType.SPARSE, SeedType.fromIdentifier("sparse"));
    Assert.assertEquals(SeedType.SPARSE, SeedType.fromIdentifier("SPARSE"));
    Assert.assertEquals(SeedType.SPARSE, SeedType.fromIdentifier("Sparse"));

    Assert.assertEquals(SeedType.ALTERNATING, SeedType.fromIdentifier("alternating"));
    Assert.assertEquals(SeedType.ALTERNATING, SeedType.fromIdentifier("ALTERNATING"));
    Assert.assertEquals(SeedType.ALTERNATING, SeedType.fromIdentifier("Alternating"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromIdentifierRejectsNull() {
    SeedType.fromIdentifier(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromIdentifierRejectsEmpty() {
    SeedType.fromIdentifier("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromIdentifierRejectsUnknown() {
    SeedType.fromIdentifier("random");
  }

  @Test
  public void testDimensionSupport() {
    Assert.assertFalse(SeedType.DENSITY.isSupported1D());
    Assert.assertTrue(SeedType.DENSITY.isSupported2D());

    Assert.assertTrue(SeedType.UNIFORM.isSupported1D());
    Assert.assertTrue(SeedType.UNIFORM.isSupported2D());

    Assert.assertTrue(SeedType.SPARSE.isSupported1D());
    Assert.assertTrue(SeedType.SPARSE.isSupported2D());

    Assert.assertTrue(SeedType.ALTERNATING.isSupported1D());
    Assert.assertTrue(SeedType.ALTERNATING.isSupported2D());
  }

  @Test
  public void testOneDimensionalValues() {
    List<SeedType> values = SeedType.oneDimensionalValues();
    Assert.assertEquals(List.of(SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING), values);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testOneDimensionalValuesUnmodifiable() {
    SeedType.oneDimensionalValues().add(SeedType.DENSITY);
  }

  @Test
  public void testTwoDimensionalValues() {
    List<SeedType> values = SeedType.twoDimensionalValues();
    Assert.assertEquals(
        List.of(SeedType.DENSITY, SeedType.UNIFORM, SeedType.SPARSE, SeedType.ALTERNATING), values);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testTwoDimensionalValuesUnmodifiable() {
    SeedType.twoDimensionalValues().remove(0);
  }
}

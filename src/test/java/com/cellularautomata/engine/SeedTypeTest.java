package com.cellularautomata.engine;

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
}

package com.backpackcloud.version;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.spectaculous.Backstage;
import org.junit.jupiter.api.Test;

import static io.smallrye.common.constraint.Assert.assertFalse;
import static io.smallrye.common.constraint.Assert.assertTrue;

public class VersionCheckTest {

  @Test
  public void testEqual() {
    assertTrue(VersionCheck.equal(Version.of("1.2.3")).test(Version.of("1.2.3")));
    assertFalse(VersionCheck.equal(Version.of("1.2.3")).test(Version.of("1.2.4")));
    assertFalse(VersionCheck.equal(Version.of("1.2.3")).test(Version.of("1.1.3")));
    assertFalse(VersionCheck.equal(Version.of("1.2.3")).test(Version.of("2.2.3")));

    assertFalse(VersionCheck.equal(Version.of("1.2")).test(Version.of("1.2.0")));
    assertFalse(VersionCheck.equal(Version.of("1")).test(Version.of("1.0")));
    assertFalse(VersionCheck.equal(Version.of("1")).test(Version.of("1.0.0")));
  }

  @Test
  public void testGreaterThan() {
    assertTrue(VersionCheck.greaterThan(Version.of("1.2.3")).test(Version.of("1.2.4")));
    assertTrue(VersionCheck.greaterThan(Version.of("1.2.3")).test(Version.of("1.3.0")));
    assertTrue(VersionCheck.greaterThan(Version.of("1.2.3")).test(Version.of("2.0.0")));

    assertFalse(VersionCheck.greaterThan(Version.of("1.2.3")).test(Version.of("1.2.3")));
    assertFalse(VersionCheck.greaterThan(Version.of("1.2.3")).test(Version.of("1.0.5")));
    assertFalse(VersionCheck.greaterThan(Version.of("1.2.3")).test(Version.of("1.1.5")));
    assertFalse(VersionCheck.greaterThan(Version.of("1.2.3")).test(Version.of("1.2")));
    assertFalse(VersionCheck.greaterThan(Version.of("1.2.3")).test(Version.of("1")));
  }

  @Test
  public void testGreaterThanOrEqual() {
    assertTrue(VersionCheck.greaterThanOrEqual(Version.of("1.2.3")).test(Version.of("1.2.4")));
    assertTrue(VersionCheck.greaterThanOrEqual(Version.of("1.2.3")).test(Version.of("1.3.0")));
    assertTrue(VersionCheck.greaterThanOrEqual(Version.of("1.2.3")).test(Version.of("2.0.0")));
    assertTrue(VersionCheck.greaterThanOrEqual(Version.of("1.2.3")).test(Version.of("1.2.3")));

    assertFalse(VersionCheck.greaterThanOrEqual(Version.of("1.2.3")).test(Version.of("1.0.5")));
    assertFalse(VersionCheck.greaterThanOrEqual(Version.of("1.2.3")).test(Version.of("1.1.5")));
    assertFalse(VersionCheck.greaterThanOrEqual(Version.of("1.2.3")).test(Version.of("1.2")));
    assertFalse(VersionCheck.greaterThanOrEqual(Version.of("1.2.3")).test(Version.of("1")));
  }

  @Test
  public void testLessThan() {
    assertFalse(VersionCheck.lessThan(Version.of("1.2.3")).test(Version.of("1.2.4")));
    assertFalse(VersionCheck.lessThan(Version.of("1.2.3")).test(Version.of("1.3.0")));
    assertFalse(VersionCheck.lessThan(Version.of("1.2.3")).test(Version.of("2.0.0")));
    assertFalse(VersionCheck.lessThan(Version.of("1.2.3")).test(Version.of("1.2.3")));

    assertTrue(VersionCheck.lessThan(Version.of("1.2.3")).test(Version.of("1.0.5")));
    assertTrue(VersionCheck.lessThan(Version.of("1.2.3")).test(Version.of("1.1.5")));
    assertTrue(VersionCheck.lessThan(Version.of("1.2.3")).test(Version.of("1.2")));
    assertTrue(VersionCheck.lessThan(Version.of("1.2.3")).test(Version.of("1")));
  }

  @Test
  public void testLessThanOrEqual() {
    assertFalse(VersionCheck.lessThanOrEqual(Version.of("1.2.3")).test(Version.of("1.2.4")));
    assertFalse(VersionCheck.lessThanOrEqual(Version.of("1.2.3")).test(Version.of("1.3.0")));
    assertFalse(VersionCheck.lessThanOrEqual(Version.of("1.2.3")).test(Version.of("2.0.0")));

    assertTrue(VersionCheck.lessThanOrEqual(Version.of("1.2.3")).test(Version.of("1.2.3")));
    assertTrue(VersionCheck.lessThanOrEqual(Version.of("1.2.3")).test(Version.of("1.0.5")));
    assertTrue(VersionCheck.lessThanOrEqual(Version.of("1.2.3")).test(Version.of("1.1.5")));
    assertTrue(VersionCheck.lessThanOrEqual(Version.of("1.2.3")).test(Version.of("1.2")));
    assertTrue(VersionCheck.lessThanOrEqual(Version.of("1.2.3")).test(Version.of("1")));
  }

  @Test
  public void testSameMajorOf() {
    assertTrue(VersionCheck.sameMajorOf(Version.of("1.2.3")).test(Version.of("1.2.3")));
    assertTrue(VersionCheck.sameMajorOf(Version.of("1.2.3")).test(Version.of("1.2.4")));
    assertTrue(VersionCheck.sameMajorOf(Version.of("1.2.3")).test(Version.of("1.1.3")));

    assertFalse(VersionCheck.sameMajorOf(Version.of("1.2.3")).test(Version.of("2.2.3")));

    assertTrue(VersionCheck.sameMajorOf(Version.of("1.2")).test(Version.of("1.2.0")));
    assertTrue(VersionCheck.sameMajorOf(Version.of("1")).test(Version.of("1.0")));
    assertTrue(VersionCheck.sameMajorOf(Version.of("1")).test(Version.of("1.0.0")));
  }

  @Test
  public void testSameMinorOf() {
    assertTrue(VersionCheck.sameMinorOf(Version.of("1.2.3")).test(Version.of("1.2.3")));
    assertTrue(VersionCheck.sameMinorOf(Version.of("1.2.3")).test(Version.of("1.2.4")));

    assertFalse(VersionCheck.sameMinorOf(Version.of("1.2.3")).test(Version.of("1.1.3")));
    assertFalse(VersionCheck.sameMinorOf(Version.of("1.2.3")).test(Version.of("2.2.3")));

    assertTrue(VersionCheck.sameMinorOf(Version.of("1.2")).test(Version.of("1.2.0")));

    Backstage.describe(VersionCheck.class)
      .when(() -> VersionCheck.sameMinorOf(Version.of("1")))
      .expect(UnbelievableException.class);
  }

  @Test
  public void testSameMicroOf() {
    assertTrue(VersionCheck.sameMicroOf(Version.of("1.2.3")).test(Version.of("1.2.3")));

    assertFalse(VersionCheck.sameMicroOf(Version.of("1.2.3")).test(Version.of("1.2.4")));
    assertFalse(VersionCheck.sameMicroOf(Version.of("1.2.3")).test(Version.of("1.1.3")));
    assertFalse(VersionCheck.sameMicroOf(Version.of("1.2.3")).test(Version.of("2.2.3")));

    Backstage.describe(VersionCheck.class)
      .when(() -> VersionCheck.sameMicroOf(Version.of("1")))
      .expect(UnbelievableException.class)

      .when(() -> VersionCheck.sameMicroOf(Version.of("1.1")))
      .expect(UnbelievableException.class);
  }

}

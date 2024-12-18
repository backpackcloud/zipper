package com.backpackcloud.version;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.spectaculous.Backstage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VersionTest {

  @Test
  public void testParsing() {
    Backstage.describe(Version.class)
      .given(Version.of("1.2.3"))

      .from(Version::toString).expect("1.2.3")
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::major).expect(1)
      .from(Version::minor).expect(2)
      .from(Version::micro).expect(3)

      .from(Version::value)
      .expect(0b0_000000000000000000011_000000000000000000101_000000000000000000111L)

      .given(Version.of("9.7.10"))

      .from(Version::toString).expect("9.7.10")
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::major).expect(9)
      .from(Version::minor).expect(7)
      .from(Version::micro).expect(10)

      .from(Version::value)
      .expect(0b0_000000000000000010011_000000000000000001111_000000000000000010101L)

      .given(Version.of("512.512.512"))

      .from(Version::toString).expect("512.512.512")
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::major).expect(512)
      .from(Version::minor).expect(512)
      .from(Version::micro).expect(512)

      .from(Version::value)
      .expect(0b0_000000000010000000001_000000000010000000001_000000000010000000001L)

      .given(Version.of("1023.1023.1023"))
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::toString).expect("1023.1023.1023")

      .from(Version::major).expect(1023)
      .from(Version::minor).expect(1023)
      .from(Version::micro).expect(1023)

      .from(Version::value)
      .expect(0b0_000000000011111111111_000000000011111111111_000000000011111111111L)

      .given(Version.of("3.0.2000"))

      .from(Version::toString).expect("3.0.2000")
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::value)
      .expect(0b0_000000000000000000111_000000000000000000001_000000000111110100001L);
  }

  @Test
  public void testSegmentLimits() {
    Backstage.describe(Version.class)
      .given(Version.of("1048575.1.1"))
      .from(Version::value)
      .expect(0b0_111111111111111111111_000000000000000000011_000000000000000000011L)

      .given(Version.of("1.1048575.1"))
      .from(Version::value)
      .expect(0b0_000000000000000000011_111111111111111111111_000000000000000000011L)

      .given(Version.of("1.1.1048575"))
      .from(Version::value)
      .expect(0b0_000000000000000000011_000000000000000000011_111111111111111111111L);
  }

  @Test
  public void testInvalidVersions() {
    Backstage.describe(Version.class)
      .when(() -> Version.of("1048576.0.1"))
      .expect(UnbelievableException.class)

      .when(() -> Version.of("1.1048576.1"))
      .expect(UnbelievableException.class)

      .when(() -> Version.of("1.1.1048576"))
      .expect(UnbelievableException.class)

      .given(Version.of("invalid"))
      .test(VersionCheck.equal(Version.NULL))

      .given(Version.of("a.b.c"))
      .test(VersionCheck.equal(Version.NULL));
  }

  @Test
  public void testCornerCases() {
    Backstage.describe(Version.class)
      .given(Version.of("7.4.2.GA"))

      .from(Version::toString).expect("7.4.2")
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::value)
      .expect(0b0_000000000000000001111_000000000000000001001_000000000000000000101L)

      .given(Version.of("1.2.3-redhat-1"))
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::major).expect(1)
      .from(Version::minor).expect(2)
      .from(Version::micro).expect(3)

      .from(Version::toString).expect("1.2.3")

      .given(Version.of("21"))

      .from(Version::precision).expect(Version.Precision.MAJOR)
      .from(Version::major).expect(21)
      .from(Version::minor).expect(0)
      .from(Version::micro).expect(0)

      .from(Version::value)
      .expect(0b0_000000000000000101011_000000000000000000000_000000000000000000000L)

      .from(Version::toString).expect("21")

      .given(Version.of("1.5"))

      .from(Version::precision).expect(Version.Precision.MINOR)
      .from(Version::major).expect(1)
      .from(Version::minor).expect(5)
      .from(Version::micro).expect(0)

      .from(Version::value)
      .expect(0b0_000000000000000000011_000000000000000001011_000000000000000000000L)

      .from(Version::toString).expect("1.5");
  }

  @Test
  public void testComparison() {
    Version a = Version.of("7.4.2");
    Version b = Version.of("7.4.1");
    Version c = Version.of("7.2.1020");
    Version d = Version.of("6.5.9");

    assertTrue(a.compareTo(b) > 0);
    assertTrue(a.compareTo(c) > 0);
    assertTrue(a.compareTo(d) > 0);
    assertTrue(c.compareTo(d) > 0);

    assertTrue(Version.of("0.0").compareTo(Version.of("0")) > 0);
    assertTrue(Version.of("0.0.0").compareTo(Version.of("0.0")) > 0);
    assertTrue(Version.of("0.0.0").compareTo(Version.of("0")) > 0);
  }

  @Test
  public void testDerivatives() {
    assertEquals(Version.of("1.2.3").withMicro(5), Version.of("1.2.5"));
    assertEquals(Version.of("1.2.3").withMinor(4), Version.of("1.4.3"));
    assertEquals(Version.of("1.2.3").withMajor(3), Version.of("3.2.3"));

    assertEquals(Version.of("1").withMajor(3), Version.of("3"));
    assertEquals(Version.of("1").withMinor(3), Version.of("1.3"));
    assertEquals(Version.of("1").withMicro(3), Version.of("1.0.3"));

    assertEquals(Version.of("1.2").withMajor(3), Version.of("3.2"));
    assertEquals(Version.of("1.2").withMinor(3), Version.of("1.3"));
    assertEquals(Version.of("1.2").withMicro(3), Version.of("1.2.3"));
  }

  @Test
  public void testNextVersions() {
    assertEquals(Version.of("1.2.3").nextMicro(), Version.of("1.2.4"));
    assertEquals(Version.of("1.2.3").nextMinor(), Version.of("1.3.3"));
    assertEquals(Version.of("1.2.3").nextMajor(), Version.of("2.2.3"));

    assertEquals(Version.of("1").nextMajor(), Version.of("2"));
    assertEquals(Version.of("1").nextMinor(), Version.of("1"));
    assertEquals(Version.of("1").nextMicro(), Version.of("1"));

    assertEquals(Version.of("1.2").nextMajor(), Version.of("2.2"));
    assertEquals(Version.of("1.2").nextMinor(), Version.of("1.3"));
    assertEquals(Version.of("1.2").nextMicro(), Version.of("1.2"));
  }

  @Test
  public void testNullValue() {
    Version nullVersion = Version.NULL;

    assertEquals(
      nullVersion.value(),
      0b0_000000000000000000000_000000000000000000000_000000000000000000000L
    );

    assertEquals(Version.Precision.NONE, nullVersion.precision());

    assertSame(nullVersion, nullVersion.nextMajor());
    assertSame(nullVersion, nullVersion.nextMinor());
    assertSame(nullVersion, nullVersion.nextMicro());

    assertSame(nullVersion, nullVersion.withMajor(10));
    assertSame(nullVersion, nullVersion.withMinor(10));
    assertSame(nullVersion, nullVersion.withMicro(10));

    assertNotEquals(nullVersion, new Version(0));
    assertNotEquals(nullVersion, new Version(0, 0));
    assertNotEquals(nullVersion, new Version(0, 0, 0));

    assertTrue(nullVersion.compareTo(new Version(0)) < 0);
    assertTrue(nullVersion.compareTo(new Version(0, 0)) < 0);
    assertTrue(nullVersion.compareTo(new Version(0, 0, 0)) < 0);
  }

  @Test
  public void testNormalization() {
    assertEquals(
      new Version(0b0_110011001111111110011_111111111111111111110_111111111111111111110L).value(),
      0b0_110011001111111110011_000000000000000000000_000000000000000000000L
    );

    assertEquals(
      new Version(0b0_110011001111111110011_111111111111111111110_111111111111111111110L).value(),
      0b0_110011001111111110011_000000000000000000000_000000000000000000000L
    );

    assertEquals(
      new Version(0b0_110011001111111110011_111111111111111111110_111110001111111111111L).value(),
      0b0_110011001111111110011_000000000000000000000_000000000000000000000L
    );

    assertEquals(
      new Version(0b0_110011001111111110010_111111111111111111110_111111111111111111110L).value(),
      0b0_000000000000000000000_000000000000000000000_000000000000000000000L
    );
  }

}

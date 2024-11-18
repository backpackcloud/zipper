package com.backpackcloud.version;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.spectaculous.Backstage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
      .expect(0b0000_00000000000000000011_00000000000000000101_00000000000000000111L)

      .given(Version.of("9.7.10"))

      .from(Version::toString).expect("9.7.10")
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::major).expect(9)
      .from(Version::minor).expect(7)
      .from(Version::micro).expect(10)

      .from(Version::value)
      .expect(0b0000_00000000000000010011_00000000000000001111_00000000000000010101L)

      .given(Version.of("512.512.512"))

      .from(Version::toString).expect("512.512.512")
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::major).expect(512)
      .from(Version::minor).expect(512)
      .from(Version::micro).expect(512)

      .from(Version::value)
      .expect(0b0000_00000000010000000001_00000000010000000001_00000000010000000001L)

      .given(Version.of("1023.1023.1023"))
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::toString).expect("1023.1023.1023")

      .from(Version::major).expect(1023)
      .from(Version::minor).expect(1023)
      .from(Version::micro).expect(1023)

      .from(Version::value)
      .expect(0b0000_00000000011111111111_00000000011111111111_00000000011111111111L)

      .given(Version.of("3.0.2000"))

      .from(Version::toString).expect("3.0.2000")
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::value)
      .expect(0b0000_00000000000000000111_00000000000000000001_00000000111110100001L);
  }

  @Test
  public void testInvalidVersions() {
    Backstage.describe(Version.class)
      .when(() -> Version.of("524288.0.1"))
      .expect(UnbelievableException.class)

      .when(() -> Version.of("1.524288.1"))
      .expect(UnbelievableException.class)

      .when(() -> Version.of("1.1.524288"))
      .expect(UnbelievableException.class);
  }

  @Test
  public void testCornerCases() {
    Backstage.describe(Version.class)
      .given(Version.of("7.4.2.GA"))

      .from(Version::toString).expect("7.4.2")
      .from(Version::precision).expect(Version.Precision.MICRO)

      .from(Version::value)
      .expect(0b0000_00000000000000001111_00000000000000001001_00000000000000000101L)

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
      .expect(0b0000_00000000000000101011_00000000000000000000_00000000000000000000L)

      .from(Version::toString).expect("21")

      .given(Version.of("1.5"))

      .from(Version::precision).expect(Version.Precision.MINOR)
      .from(Version::major).expect(1)
      .from(Version::minor).expect(5)
      .from(Version::micro).expect(0)

      .from(Version::value)
      .expect(0b0000_00000000000000000011_00000000000000001011_00000000000000000000L)

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

}

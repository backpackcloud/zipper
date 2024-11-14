package com.backpackcloud.version;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.spectaculous.Backstage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SemanticVersionTest {

  @Test
  public void testParsing() {
    Backstage.describe(SemanticVersion.class)
      .given(SemanticVersion.from("1.2.3"))

      .from(SemanticVersion::toString).expect("1.2.3")

      .from(SemanticVersion::major).expect(1)
      .from(SemanticVersion::minor).expect(2)
      .from(SemanticVersion::micro).expect(3)

      .from(SemanticVersion::value)
      .expect(0b0000_00000000000000000001_00000000000000000010_00000000000000000011L)

      .given(SemanticVersion.from("9.7.10"))

      .from(SemanticVersion::toString).expect("9.7.10")

      .from(SemanticVersion::major).expect(9)
      .from(SemanticVersion::minor).expect(7)
      .from(SemanticVersion::micro).expect(10)

      .from(SemanticVersion::value)
      .expect(0b0000_00000000000000001001_00000000000000000111_00000000000000001010L)

      .given(SemanticVersion.from("512.512.512"))

      .from(SemanticVersion::toString).expect("512.512.512")

      .from(SemanticVersion::major).expect(512)
      .from(SemanticVersion::minor).expect(512)
      .from(SemanticVersion::micro).expect(512)

      .from(SemanticVersion::value)
      .expect(0b0000_00000000001000000000_00000000001000000000_00000000001000000000L)

      .given(SemanticVersion.from("1023.1023.1023"))

      .from(SemanticVersion::toString).expect("1023.1023.1023")

      .from(SemanticVersion::major).expect(1023)
      .from(SemanticVersion::minor).expect(1023)
      .from(SemanticVersion::micro).expect(1023)

      .from(SemanticVersion::value)
      .expect(0b0000_00000000001111111111_00000000001111111111_00000000001111111111L)

      .when(() -> SemanticVersion.from("1024.0.1"))
      .expect(UnbelievableException.class)

      .given(SemanticVersion.from("3.0.2000"))

      .from(SemanticVersion::toString).expect("3.0.2000")

      .from(SemanticVersion::value)
      .expect(0b0000_00000000000000000011_00000000000000000000_00000000011111010000L)

      .test(version -> !version.equals(SemanticVersion.from("7.4.2")));
  }

  @Test
  public void testCornerCases() {
    Backstage.describe(SemanticVersion.class)
      .given(SemanticVersion.from("7.4.2.GA"))

      .from(SemanticVersion::toString).expect("7.4.2")

      .from(SemanticVersion::value)
      .expect(0b0000_00000000000000000111_00000000000000000100_00000000000000000010L)

      .given(SemanticVersion.from("1.2.3-redhat-1"))

      .from(SemanticVersion::major).expect(1)
      .from(SemanticVersion::minor).expect(2)
      .from(SemanticVersion::micro).expect(3)

      .from(SemanticVersion::toString).expect("1.2.3")

      .given(SemanticVersion.from("21"))

      .from(SemanticVersion::major).expect(21)
      .from(SemanticVersion::minor).expect(0)
      .from(SemanticVersion::micro).expect(0)

      .from(SemanticVersion::toString).expect("21.0.0");
  }

  @Test
  public void testComparison() {
    SemanticVersion a = SemanticVersion.from("7.4.2");
    SemanticVersion b = SemanticVersion.from("7.4.1");
    SemanticVersion c = SemanticVersion.from("7.2.1020");
    SemanticVersion d = SemanticVersion.from("6.5.9");

    assertTrue(a.compareTo(b) > 0);
    assertTrue(a.compareTo(c) > 0);
    assertTrue(a.compareTo(d) > 0);
    assertTrue(c.compareTo(d) > 0);
  }

}

package com.backpackcloud.version;

import com.backpackcloud.UnbelievableException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public class SemanticVersion implements Comparable<SemanticVersion> {

  private final long value;

  private SemanticVersion(long value) {
    this.value = value;
  }

  public long value() {
    return value;
  }

  public int major() {
    return (int) (this.value >>> 40);
  }

  public int minor() {
    return (int) ((this.value >>> 20) & 0b0000_00000000000000000000_11111111111111111111);
  }

  public int micro() {
    return (int) (this.value & 0b0000_00000000000000000000_00000000000000000000_11111111111111111111);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SemanticVersion that)) return false;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public int compareTo(SemanticVersion o) {
    return Long.compare(this.value, o.value);
  }

  @Override
  public String toString() {
    return String.format("%d.%d.%d", major(), minor(), micro());
  }

  public static final SemanticVersion NULL = new SemanticVersion(0);

  @JsonCreator
  public static SemanticVersion from(String value) {
    if (value == null || value.isBlank()) {
      return NULL;
    }
    String[] tokens = value.split("\\.", 3);
    int[] values = new int[3];
    for (int i = 0; i < values.length && i < tokens.length; i++) {
      String segment = tokens[i].replaceAll("\\D.*", "");
      if (!segment.isBlank()) {
        values[i] = Integer.parseInt(segment);
        // threshold = 2 ^ 20
        if (values[i] >= 1048576) {
          throw new UnbelievableException("version >= 1048576: " + values[i]);
        }
      }
    }
    return new SemanticVersion((long) values[0] << 40 | (long) values[1] << 20 | values[2]);
  }

}

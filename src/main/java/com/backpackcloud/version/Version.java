package com.backpackcloud.version;

import com.backpackcloud.UnbelievableException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public class Version implements Comparable<Version> {

  private static final int SEGMENT_MAJOR = 41;
  private static final int SEGMENT_MINOR = 21;
  private static final int SEGMENT_MICRO = 1;

  private static final int MAX = 524287;

  private final long value;
  private final Precision precision;

  private Version(long value) {
    if (value > 0b0000_11111111111111111111_11111111111111111111_11111111111111111111L) {
      throw new UnbelievableException("Invalid value");
    }
    this.value = value;

    boolean knownMicro = (this.value & 0b0000_00000000000000000000_00000000000000000000_00000000000000000001L) ==
      0b0000_00000000000000000000_00000000000000000000_00000000000000000001L;

    boolean knownMinor = (this.value & 0b0000_00000000000000000000_00000000000000000001_00000000000000000000L) ==
      0b0000_00000000000000000000_00000000000000000001_00000000000000000000L;

    if (knownMicro && knownMinor) {
      this.precision = Precision.MICRO;
    } else if (knownMinor) {
      this.precision = Precision.MINOR;
    } else {
      this.precision = Precision.MAJOR;
    }
  }

  public Version(int major, int minor, int micro) {
    this(((long) major << SEGMENT_MAJOR | (long) minor << SEGMENT_MINOR | (long) micro << SEGMENT_MICRO) |
      0b0000_00000000000000000001_00000000000000000001_00000000000000000001L);

    if (major > MAX || minor > MAX || micro > MAX) {
      throw new UnbelievableException("Invalid value");
    }
  }

  public Version(int major, int minor) {
    this(((long) major << SEGMENT_MAJOR | (long) minor << SEGMENT_MINOR) |
      0b0000_00000000000000000001_00000000000000000001_00000000000000000000L);

    if (major > MAX || minor > MAX) {
      throw new UnbelievableException("Invalid value");
    }
  }

  public Version(int major) {
    this(((long) major << SEGMENT_MAJOR) |
      0b0000_00000000000000000001_00000000000000000000_00000000000000000000L);

    if (major > MAX) {
      throw new UnbelievableException("Invalid value");
    }
  }

  public long value() {
    return value;
  }

  public int major() {
    return segment(SEGMENT_MAJOR);
  }

  public int minor() {
    return segment(SEGMENT_MINOR);
  }

  public int micro() {
    return segment(SEGMENT_MICRO);
  }

  public Version withMajor(int newMajor) {
    return switch (precision) {
      case MAJOR -> new Version(newMajor);
      case MINOR -> new Version(newMajor, minor());
      case MICRO -> new Version(newMajor, minor(), micro());
    };
  }

  public Version withMinor(int newMinor) {
    return switch (precision) {
      case MAJOR, MINOR -> new Version(major(), newMinor);
      case MICRO -> new Version(major(), newMinor, micro());
    };
  }

  public Version withMicro(int newMicro) {
    return new Version(major(), minor(), newMicro);
  }

  public Precision precision() {
    return precision;
  }

  private int segment(int segmentFactor) {
    return (int) ((this.value >>> segmentFactor) & 0b0000_00000000000000000000_00000000000000000000_01111111111111111111);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Version that)) return false;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public int compareTo(Version o) {
    return Long.compare(this.value, o.value);
  }

  @Override
  public String toString() {
    return switch (precision) {
      case MAJOR -> String.valueOf(major());
      case MINOR -> String.format("%d.%d", major(), minor());
      case MICRO -> String.format("%d.%d.%d", major(), minor(), micro());
    };
  }

  public static final Version NULL = new Version(0);

  @JsonCreator
  public static Version of(String value) {
    if (value == null || value.isBlank()) {
      return NULL;
    }
    String[] tokens = value.split("\\.", 3);
    int[] values = new int[3];
    for (int i = 0; i < values.length && i < tokens.length; i++) {
      String segment = tokens[i].replaceAll("\\D.*", "");
      if (!segment.isBlank()) {
        values[i] = Integer.parseInt(segment);
      }
    }
    if (tokens.length == 3) {
      return new Version(
        values[0],
        values[1],
        values[2]
      );
    } else if (tokens.length == 2) {
      return new Version(
        values[0],
        values[1]
      );
    } else if (tokens.length == 1) {
      return new Version(
        values[0]
      );
    }

    throw new UnbelievableException();
  }

  public enum Precision {
    MAJOR, MINOR, MICRO
  }

}

package com.backpackcloud.version;

import com.backpackcloud.UnbelievableException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.infinispan.api.annotations.indexing.Basic;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.Objects;

/// A simple and lightweight three-segment version implementation.
///
/// The segments are "major", "minor" and "micro". They are stored on a single <code>long</code> field with a
/// predetermined length of 20 bits for each segment. That means the theoretical maximum value of a segment is
/// <code>(2 ^ 20) - 1 = 1_048_576</code>.
///
/// It's possible to have a version object without the three segments, and that won't change their position
/// in the field, so the objects are comparable regardless of how many segments they have.
///
/// By convention, the absence of a segment is treated as less value than the segment of a value <code>zero</code>,
/// so the version <code>2.0.0</code> is greater than the version <code>2.0</code>.
///
/// @author Ataxexe
public class Version implements Comparable<Version> {

  // Variables that indicates how many bits we should shift to get to each segment
  // Notice that there's a flag before each segment value, so that's why we need to add 1
  private static final int SEGMENT_MAJOR = 43;
  private static final int SEGMENT_MINOR = 22;
  private static final int SEGMENT_MICRO = 1;
  // --------------------------------------------------
  /// The theoretical maximum value of a segment
  private static final int MAX = 0b11111111111111111111;

  /// Stores all the information about this version.
  ///
  /// There's a total of 21 bits allocated to each segment, being the first one a flag to indicate whether
  /// the segment is counted. This allows us to use zeroes as both a value (when the flag is set to <code>one</code>)
  /// or nothing (when the flag is set to <code>zero</code>).
  ///
  /// The remaining 20 bits are used to store the actual segment value. This leaves us with exactly 63 bits to store
  /// the values and flags, saving the last bit to avoid dealing with negative numbers.
  @Basic(aggregable = true, projectable = true, sortable = true, searchable = true)
  @ProtoField(1)
  private final long value;
  private final Precision precision;

  /**
   * Creates a new Version object using the given internal representation.
   *
   * @param value the internal representation of this version
   * @see #Version(int)  Version
   * @see #Version(int, int) Version
   * @see #Version(int, int, int) Version
   */
  @ProtoFactory
  public Version(long value) {
    if (value < 0) {
      throw new UnbelievableException("Invalid value");
    }

    boolean knownMicro = (value & 0b0_000000000000000000000_000000000000000000000_000000000000000000001L) ==
      0b0_000000000000000000000_000000000000000000000_000000000000000000001L;

    boolean knownMinor = (value & 0b0_000000000000000000000_000000000000000000001_000000000000000000000L) ==
      0b0_000000000000000000000_000000000000000000001_000000000000000000000L;

    boolean knownMajor = (value & 0b0_000000000000000000001_000000000000000000000_000000000000000000000L) ==
      0b0_000000000000000000001_000000000000000000000_000000000000000000000L;

    // normalizes the internal implementation by zeroing segments that are not part of the precision
    if (knownMicro && knownMinor && knownMajor) {
      this.precision = Precision.MICRO;
      this.value = value;

    } else if (knownMinor && knownMajor) {
      this.precision = Precision.MINOR;
      this.value = value & 0b0_111111111111111111111_111111111111111111111_000000000000000000000L;

    } else if (knownMajor) {
      this.precision = Precision.MAJOR;
      this.value = value & 0b0_111111111111111111111_000000000000000000000_000000000000000000000L;

    } else {
      precision = Precision.NONE;
      this.value = 0;
    }
  }

  /**
   * Creates a version object with all the three segments present.
   *
   * @param major the value of the major segment
   * @param minor the value of the minor segment
   * @param micro the value of the micro segment
   */
  public Version(int major, int minor, int micro) {
    this(((long) major << SEGMENT_MAJOR | (long) minor << SEGMENT_MINOR | (long) micro << SEGMENT_MICRO) |
      0b0_000000000000000000001_000000000000000000001_000000000000000000001L);

    if (major > MAX || minor > MAX || micro > MAX) {
      throw new UnbelievableException("Invalid value");
    }
  }

  /**
   * Creates a version object with only the first two segments.
   *
   * @param major the value of the major segment
   * @param minor the value of the minor segment
   */
  public Version(int major, int minor) {
    this(((long) major << SEGMENT_MAJOR | (long) minor << SEGMENT_MINOR) |
      0b0_000000000000000000001_000000000000000000001_000000000000000000000L);

    if (major > MAX || minor > MAX) {
      throw new UnbelievableException("Invalid value");
    }
  }

  /**
   * Creates a version object with only the first segment.
   *
   * @param major the value of the major segment
   */
  public Version(int major) {
    this(((long) major << SEGMENT_MAJOR) |
      0b0_000000000000000000001_000000000000000000000_000000000000000000000L);

    if (major > MAX) {
      throw new UnbelievableException("Invalid value");
    }
  }

  /**
   * Returns the internal representation of this version, for storing purposes.
   *
   * @return the internal representation of this version.
   */
  public long value() {
    return value;
  }

  /**
   * Returns the major segment of this version.
   *
   * @return the major segment
   */
  public int major() {
    return segment(SEGMENT_MAJOR);
  }

  /**
   * Returns the minor segment of this version. If this version doesn't have a minor segment, <code>zero</code>
   * is returned.
   *
   * @return the minor segment
   * @see #precision()
   */
  public int minor() {
    return segment(SEGMENT_MINOR);
  }

  /**
   * Returns the micro segment of this version. If this version doesn't have a micro segment, <code>zero</code>
   * is returned.
   *
   * @return the micro segment
   * @see #precision()
   */
  public int micro() {
    return segment(SEGMENT_MICRO);
  }

  /**
   * Creates a new version with the major segment incremented by <code>one</code>. Preserves the {@link #precision()}.
   *
   * @return a new version <code>X+1.Y.Z</code>
   */
  public Version nextMajor() {
    return switch (precision) {
      case NONE -> NULL;
      case MAJOR -> new Version(major() + 1);
      case MINOR -> new Version(major() + 1, minor());
      case MICRO -> new Version(major() + 1, minor(), micro());
    };
  }

  /**
   * Creates a new version with the minor segment incremented by <code>one</code> (if possible).
   * Preserves the {@link #precision()}.
   *
   * @return a new version <code>X.Y+1.Z</code>
   */
  public Version nextMinor() {
    return switch (precision) {
      case NONE -> NULL;
      case MAJOR -> new Version(major());
      case MINOR -> new Version(major(), minor() + 1);
      case MICRO -> new Version(major(), minor() + 1, micro());
    };
  }

  /**
   * Creates a new version with the micro segment incremented by <code>one</code> (if possible).
   * Preserves the {@link #precision()}.
   *
   * @return a new version <code>X.Y.Z+1</code>
   */
  public Version nextMicro() {
    return switch (precision) {
      case NONE -> NULL;
      case MAJOR -> new Version(major());
      case MINOR -> new Version(major(), minor());
      case MICRO -> new Version(major(), minor(), micro() + 1);
    };
  }

  /**
   * Creates a new version with the given value as its major segment.
   *
   * @param newMajor the value of the major segment
   * @return a new version with the given major segment.
   */
  public Version withMajor(int newMajor) {
    return switch (precision) {
      case NONE -> NULL;
      case MAJOR -> new Version(newMajor);
      case MINOR -> new Version(newMajor, minor());
      case MICRO -> new Version(newMajor, minor(), micro());
    };
  }

  /**
   * Creates a new version with the given value as its minor segment.
   *
   * @param newMinor the value of the minor segment
   * @return a new version with the given minor segment.
   */
  public Version withMinor(int newMinor) {
    return switch (precision) {
      case NONE -> NULL;
      case MAJOR, MINOR -> new Version(major(), newMinor);
      case MICRO -> new Version(major(), newMinor, micro());
    };
  }

  /**
   * Creates a new version with the given value as its micro segment.
   *
   * @param newMicro the value of the micro segment
   * @return a new version with the given micro segment.
   */
  public Version withMicro(int newMicro) {
    if (precision == Precision.NONE) {
      return Version.NULL;
    }
    return new Version(major(), minor(), newMicro);
  }

  /**
   * Return the precision which this version value is stored.
   *
   * @return the precision of this version value.
   */
  public Precision precision() {
    return precision;
  }

  private int segment(int segmentFactor) {
    return (int) ((this.value >>> segmentFactor) & 0b0_000000000000000000000_000000000000000000000_011111111111111111111);
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
      case NONE -> "null";
      case MAJOR -> String.valueOf(major());
      case MINOR -> String.format("%d.%d", major(), minor());
      case MICRO -> String.format("%d.%d.%d", major(), minor(), micro());
    };
  }

  /// The representation of a Version <code>null</code>.
  public static final Version NULL = new Version(0L);

  /**
   * Tries to parse the given value in the format <code>major.minor.micro</code>. Returns {@link #NULL}
   * if it's not possible to parse the input.
   *
   * @param value the value to parse
   * @return the Version representation of the given string, or {@link #NULL} if not possible.
   */
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
        try {
          values[i] = Integer.parseInt(segment);
        } catch (NumberFormatException e) {
          return NULL;
        }
      } else {
        return NULL;
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

    return NULL;
  }

  /// Enumeration of the possible precision values a version object can have.
  ///
  /// The precision essentially tells which segments are used to determine a version value.
  /// A segment might hold the value <code>zero</code>, but doesn't necessarily imply that
  /// it's used to either format the version or to check its precedence with other versions.
  ///
  /// @author Ataxexe
  public enum Precision {
    /// Used for {@link Version#NULL} values. If a version has this precision, its internal value is <code>0</code>.
    NONE,
    /// Indicates that only the major segment is used.
    MAJOR,
    /// Indicates that only the major and minor segments are used.
    MINOR,
    /// Indicates that all segments are used.
    MICRO
  }

}

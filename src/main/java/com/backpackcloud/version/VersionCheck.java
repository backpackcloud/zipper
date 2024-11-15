package com.backpackcloud.version;

import com.backpackcloud.UnbelievableException;

import java.util.function.Predicate;

public final class VersionCheck {

  private VersionCheck() {

  }

  public static Predicate<Version> equal(Version reference) {
    return version -> version.compareTo(reference) == 0;
  }

  public static Predicate<Version> lessThan(Version reference) {
    return version -> version.compareTo(reference) < 0;
  }

  public static Predicate<Version> lessThanOrEqual(Version reference) {
    return lessThan(reference).or(equal(reference));
  }

  public static Predicate<Version> greaterThan(Version reference) {
    return version -> version.compareTo(reference) > 0;
  }

  public static Predicate<Version> greaterThanOrEqual(Version reference) {
    return greaterThan(reference).or(equal(reference));
  }

  public static Predicate<Version> sameMajorOf(Version reference) {
    return version -> version.major() == reference.major();
  }

  public static Predicate<Version> sameMinorOf(Version reference) {
    if (reference.precision() == Version.Precision.MAJOR) {
      throw new UnbelievableException("Reference version doesn't have enough precision.");
    }
    return sameMajorOf(reference)
      .and(version -> version.precision() != Version.Precision.MAJOR)
      .and(version -> version.minor() == reference.minor());
  }

  public static Predicate<Version> sameMicroOf(Version reference) {
    if (reference.precision() != Version.Precision.MICRO) {
      throw new UnbelievableException("Reference version doesn't have enough precision.");
    }
    return sameMajorOf(reference)
      .and(sameMinorOf(reference))
      .and(version -> version.precision() == Version.Precision.MICRO)
      .and(version -> version.micro() == reference.micro());
  }

}

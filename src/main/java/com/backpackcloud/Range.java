package com.backpackcloud;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Range<T> {

  private final Supplier<T> start;
  private final Predicate<T> endPredicate;
  private final UnaryOperator<T> accumulator;

  public Range(Supplier<T> start, Predicate<T> endPredicate, UnaryOperator<T> accumulator) {
    this.start = start;
    this.endPredicate = endPredicate;
    this.accumulator = accumulator;
  }

  public boolean includes(T element) {
    return stream().anyMatch(obj -> obj.equals(element));
  }

  public Stream<T> stream() {
    return Stream.iterate(start.get(), endPredicate.negate(), accumulator);
  }

}

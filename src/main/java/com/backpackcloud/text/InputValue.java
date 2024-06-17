package com.backpackcloud.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQuery;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@RegisterForReflection
public interface InputValue extends Supplier<String> {

  @JsonValue
  @Override
  String get();

  default Optional<String> text() {
    String value = get();
    return value == null || value.isEmpty() ? Optional.empty() : Optional.of(value);
  }

  default <T> Optional<T> map(Function<String, T> mapper) {
    return text().map(mapper);
  }

  default Optional<Integer> integer() {
    try {
      return map(Integer::parseInt);
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  default Optional<Long> number() {
    try {
      return map(Long::parseLong);
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  default Optional<Double> decimal() {
    try {
      return map(Double::parseDouble);
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  default Optional<Boolean> bool() {
    return map(Boolean::parseBoolean);
  }

  default <T> Optional<T> temporal(String pattern, TemporalQuery<T> query) {
    return map(input -> DateTimeFormatter.ofPattern(pattern).parse(input, query));
  }

  default <T extends Enum<T>> Optional<T> identify(Class<T> enumType) {
    try {
      return map(String::toUpperCase)
        .map(input -> input.replaceAll("([- .])", "_"))
        .map(input -> Enum.valueOf(enumType, input));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  @JsonCreator
  static InputValue of(String input) {
    return () -> input;
  }

  static InputValue of(Supplier<String> input) {
    return input::get;
  }

}

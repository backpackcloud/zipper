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

  default String asString() {
    String value = get();
    return value == null ? "" : value;
  }

  default String or(String defaultValue) {
    String value = asString();
    return value.isEmpty() ? defaultValue : value;
  }

  default <R> Optional<R> map(Function<String, R> function) {
    String value = asString();
    return value.isEmpty() ? Optional.empty() : Optional.ofNullable(function.apply(asString()));
  }

  default int asInt() {
    return Integer.parseInt(asString());
  }

  default int or(int defaultValue) {
    String value = asString();
    return value.isEmpty() ? defaultValue : asInt();
  }

  default long asLong() {
    return Long.parseLong(asString());
  }

  default long or(long defaultValue) {
    String value = asString();
    return value.isEmpty() ? defaultValue : asLong();
  }

  default boolean asBoolean() {
    return Boolean.parseBoolean(asString());
  }

  default boolean or(boolean defaultValue) {
    String value = asString();
    return value.isEmpty() ? defaultValue : asBoolean();
  }

  default <T extends Enum<T>> T asEnum(Class<T> enumType) {
    return Enum.valueOf(enumType, asString().toUpperCase().replaceAll("([- .])", "_"));
  }

  default <T extends Enum<T>> T or(T defaultValue) {
    String value = asString();
    return value.isEmpty() ? defaultValue : asEnum(defaultValue.getDeclaringClass());
  }

  default <T> T asTemporal(String pattern, TemporalQuery<T> query) {
    return DateTimeFormatter.ofPattern(pattern).parse(asString(), query);
  }

  @JsonCreator
  static InputValue of(String input) {
    return () -> input;
  }

}

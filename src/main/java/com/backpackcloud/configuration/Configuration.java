/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Marcelo Guimarães
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.backpackcloud.configuration;

import com.backpackcloud.UnbelievableException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

/**
 * Interface that defines a configuration that can be supplied via different sources.
 *
 * @author Marcelo Guimarães
 */
@RegisterForReflection
@JsonDeserialize(using = ConfigurationDeserializer.class)
public interface Configuration extends Supplier<String> {

  /**
   * Represents a not supplied configuration.
   */
  Configuration NOT_SUPPLIED = new NotSuppliedConfiguration();

  /**
   * Checks if this source has some configuration set.
   *
   * @return {@code true} if this source holds any configuration value.
   */
  boolean isSet();

  /**
   * Gets the value of this configuration.
   *
   * @return the configuration value.
   */
  String get();

  /**
   * Gets the value of this configuration as an {@code int}.
   *
   * @return an {@code int} value defined by this configuration.
   */
  default int asInt() {
    return Integer.parseInt(get());
  }

  /**
   * Gets the value of this configuration as an {@code long}.
   *
   * @return a {@code long} value defined by this configuration.
   */
  default long asLong() {
    return Long.parseLong(get());
  }

  /**
   * Gets the value of this configuration as an {@code boolean}.
   *
   * @return a {@code boolean} value defined by this configuration.
   */
  default boolean asBoolean() {
    return Boolean.parseBoolean(get());
  }

  /**
   * Assumes this configuration value is pointing to an external location
   * and reads the value at that location.
   *
   * @return the value stored in the location defined by this configuration.
   */
  default String read() {
    try {
      return Files.readString(Path.of(get()));
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  /**
   * Assumes this configuration value is pointing to an external location
   * and reads the lines at that location.
   *
   * @return the lines stored in the location defined by this configuration.
   */
  default List<String> readLines() {
    try {
      return Files.readAllLines(Path.of(get()));
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  /**
   * Returns this configuration if it's set or the given default
   * configuration otherwise.
   *
   * @param defaultConfiguration the default configuration to return
   * @return this configuration or the default one
   */
  default Configuration or(Configuration defaultConfiguration) {
    return isSet() ? this : defaultConfiguration;
  }

  default String or(Supplier<String> supplier) {
    return isSet() ? get() : supplier.get();
  }

  /**
   * Returns this configuration value if it's {@link #isSet() set}, otherwise
   * returns the given {@code defaultValue}.
   *
   * @param defaultValue the default value to return
   * @return this configuration value or the default value in case this
   * configuration is not set.
   */
  default String orElse(String defaultValue) {
    return isSet() ? get() : defaultValue;
  }

  /**
   * Returns this configuration value if it's {@link #isSet() set}, otherwise
   * returns the given {@code defaultValue}.
   *
   * @param defaultValue the default value to return
   * @return this configuration value or the default value in case this
   * configuration is not set.
   */
  default int orElse(int defaultValue) {
    return isSet() ? asInt() : defaultValue;
  }

  /**
   * Returns this configuration value if it's {@link #isSet() set}, otherwise
   * returns the given {@code defaultValue}.
   *
   * @param defaultValue the default value to return
   * @return this configuration value or the default value in case this
   * configuration is not set.
   */
  default long orElse(long defaultValue) {
    return isSet() ? asLong() : defaultValue;
  }

  /**
   * Returns this configuration value if it's {@link #isSet() set}, otherwise
   * returns the given {@code defaultValue}.
   *
   * @param defaultValue the default value to return
   * @return this configuration value or the default value in case this
   * configuration is not set.
   */
  default boolean orElse(boolean defaultValue) {
    return isSet() ? asBoolean() : defaultValue;
  }

  static ConfigurationChain configuration() {
    return new ConfigurationChain(new NotSuppliedConfiguration());
  }

}

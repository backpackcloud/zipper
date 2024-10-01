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

import java.util.List;
import java.util.function.Supplier;

public class ConfigurationChain implements Configuration {

  private final Configuration configuration;

  public ConfigurationChain(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public boolean isSet() {
    return configuration.isSet();
  }

  @Override
  public String get() {
    return configuration.get();
  }

  @Override
  public int asInt() {
    return configuration.asInt();
  }

  @Override
  public long asLong() {
    return configuration.asLong();
  }

  @Override
  public boolean asBoolean() {
    return configuration.asBoolean();
  }

  @Override
  public String read() {
    return configuration.read();
  }

  @Override
  public List<String> readLines() {
    return configuration.readLines();
  }

  @Override
  public Configuration or(Configuration defaultConfiguration) {
    return configuration.or(defaultConfiguration);
  }

  @Override
  public String or(Supplier<String> supplier) {
    return configuration.or(supplier);
  }

  @Override
  public String orElse(String defaultValue) {
    return configuration.orElse(defaultValue);
  }

  @Override
  public int orElse(int defaultValue) {
    return configuration.orElse(defaultValue);
  }

  @Override
  public long orElse(long defaultValue) {
    return configuration.orElse(defaultValue);
  }

  @Override
  public boolean orElse(boolean defaultValue) {
    return configuration.orElse(defaultValue);
  }

  public ConfigurationChain env(String key) {
    return new ConfigurationChain(configuration.or(Configuration.env(key)));
  }

  public ConfigurationChain file(String key) {
    return new ConfigurationChain(configuration.or(Configuration.file(key)));
  }

  public ConfigurationChain resource(ClassLoader classLoader, String key) {
    return new ConfigurationChain(configuration.or(Configuration.resource(classLoader, key)));
  }

  public ConfigurationChain resource(String key) {
    return new ConfigurationChain(configuration.or(Configuration.resource(Thread.currentThread().getContextClassLoader(), key)));
  }

  public ConfigurationChain property(String key) {
    return new ConfigurationChain(configuration.or(Configuration.property(key)));
  }

  public ConfigurationChain url(String key) {
    return new ConfigurationChain(configuration.or(Configuration.url(key)));
  }

  public ConfigurationChain value(String key) {
    return new ConfigurationChain(configuration.or(Configuration.value(key)));
  }

}

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

import java.util.function.Supplier;
import java.util.stream.Stream;

public class ConfigurationSupplier implements Supplier<Configuration> {

  private final String name;

  public ConfigurationSupplier(String name) {
    this.name = name;
  }

  public Configuration get() {
    return Stream.of(fromEnvironment(),
        fromSystemProperty(),
        fromWorkingDir(),
        fromUserHome())
      .filter(Configuration::isSet)
      .findFirst()
      .orElseGet(this::getDefault);
  }

  public Configuration fromEnvironment() {
    String path = System.getenv(name.toUpperCase() + "_CONFIG_FILE");
    return path != null ? new FileConfiguration(path) : Configuration.NOT_SUPPLIED;
  }

  public Configuration fromSystemProperty() {
    return new FileConfiguration(System.getProperty("user.home") + "/" + name + ".yml");
  }

  public Configuration fromWorkingDir() {
    return new FileConfiguration("./" + name + ".yml");
  }

  public Configuration fromUserHome() {
    return new FileConfiguration(System.getProperty("user.home") + "/" + name + ".yml");
  }

  public Configuration getDefault() {
    return new ResourceConfiguration("META-INF/" + name + ".yml");
  }

}

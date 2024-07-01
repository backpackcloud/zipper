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

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class UserConfigurationLoader {

  private final String name;

  public UserConfigurationLoader(String name) {
    this.name = name;
  }

  public Optional<String> resolveLocation() {
    return Stream.of(System.getenv(name.toUpperCase() + "_CONFIG_FILE"),
        System.getProperty(name + ".config.file"),
        "./" + name + ".yml",
        System.getProperty("user.home") + "/" + name + ".yml")
      .filter(Objects::nonNull)
      .map(File::new)
      .filter(File::exists)
      .findFirst()
      .map(File::getPath);
  }

  public Configuration resolve() {
    return resolveLocation()
      .<Configuration>map(FileConfiguration::new)
      .orElseGet(this::getDefault);
  }

  public Configuration getDefault() {
    return new ResourceConfiguration("META-INF/" + name + ".yml");
  }

}

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

package com.backpackcloud.cli.ui.impl;

import com.backpackcloud.cli.ui.Color;
import com.backpackcloud.cli.ui.ColorMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultColorMap implements ColorMap {

  private final Map<String, String> colors;

  public DefaultColorMap(Map<String, String> colors) {
    this.colors = colors;
  }

  @Override
  public Optional<Color> colorOf(String key) {
    String value = colors.get(key);
    if (colors.containsKey(value)) {
      return colorOf(value);
    }
    return Optional.ofNullable(value).map(Color::parse);
  }

  @Override
  public void put(String key, String color) {
    colors.put(key, color);
  }

  @Override
  public Set<String> colors() {
    return new HashSet<>(colors.keySet());
  }

  @Override
  public String valueOf(String key) {
    return colors.get(key);
  }

}

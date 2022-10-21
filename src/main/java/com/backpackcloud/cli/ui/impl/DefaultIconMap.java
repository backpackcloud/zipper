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

import com.backpackcloud.cli.ui.IconMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultIconMap implements IconMap {

  private final Map<String, String> icons;

  public DefaultIconMap(Map<String, String> icons) {
    this.icons = icons;
  }

  @Override
  public String symbolOf(String icon) {
    if (icons.containsKey(icon)) {
      String symbol = icons.get(icon);
      if (icons.containsKey(symbol)) {
        return symbolOf(symbol);
      }
      return symbol;
    }
    return "";
  }

  @Override
  public Set<String> icons() {
    return new HashSet<>(icons.keySet());
  }

  @Override
  public void put(String name, String symbol) {
    this.icons.put(name, symbol);
  }

  @Override
  public void remove(String name) {
    this.icons.remove(name);
  }

}

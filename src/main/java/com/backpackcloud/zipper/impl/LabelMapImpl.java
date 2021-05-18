/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Marcelo Guimarães <ataxexe@backpackcloud.com>
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

package com.backpackcloud.zipper.impl;

import com.backpackcloud.zipper.Label;
import com.backpackcloud.zipper.LabelMap;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.*;

public class LabelMapImpl implements LabelMap {

  @JsonValue
  private final Map<String, Label> map;

  @JsonCreator
  public LabelMapImpl(Map<String, String> map) {
    this.map = new HashMap<>(map.size());
    map.forEach((key, value) -> this.map.put(key, new Label(key, value)));
  }

  public LabelMapImpl(List<Label> labels) {
    this.map = new HashMap<>(labels.size());
    labels.forEach(label -> map.put(label.name(), label));
  }

  @Override
  public void add(Label label) {
    map.put(label.name(), label);
  }

  @Override
  public Optional<Label> get(String name) {
    return Optional.ofNullable(map.get(name));
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public Collection<Label> all() {
    return map.values();
  }

}

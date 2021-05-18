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

package com.backpackcloud.zipper;

import com.backpackcloud.zipper.impl.LabelMapImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonDeserialize(as = LabelMapImpl.class)
@JsonSerialize(as = LabelMapImpl.class)
public interface LabelMap {

  void add(Label label);

  Optional<Label> get(String name);

  boolean isEmpty();

  int size();

  Collection<Label> all();

  static LabelMap of(Map<String, String> values) {
    return new LabelMapImpl(values);
  }

  static LabelMap empty() {
    return new LabelMapImpl(new HashMap<>());
  }

  static LabelMap immutable(LabelMap labelMap) {
    return new LabelMap() {
      @Override
      public void add(Label label) {
        throw new UnbelievableException("Cannot add a tag to an Immutable TagMap");
      }

      @Override
      public Optional<Label> get(String name) {
        return labelMap.get(name);
      }

      @Override
      public boolean isEmpty() {
        return labelMap.isEmpty();
      }

      @Override
      public int size() {
        return labelMap.size();
      }

      @Override
      public Collection<Label> all() {
        return labelMap.all();
      }
    };
  }

}

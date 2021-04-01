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

import com.backpackcloud.zipper.Selector;
import com.backpackcloud.zipper.TagMap;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SelectorImpl implements Selector {

  private final List<Predicate<TagMap>> predicates;

  @JsonCreator
  public SelectorImpl(Map<String, String> values) {
    predicates = new ArrayList<>(values.size());

    values.forEach((key, value) -> {
      Predicate<TagMap> predicate = null;

      for (String v : value.split("\\s*\\|\\s*")) {
        if (predicate == null) {
          predicate = createPredicate(key, v);
        } else {
          predicate = predicate.or(createPredicate(key, v));
        }
      }

      predicates.add(predicate);
    });
  }

  public SelectorImpl(List<Predicate<TagMap>> predicates) {
    this.predicates = predicates;
  }

  @Override
  public boolean test(TagMap tagMap) {
    return predicates.stream().allMatch(predicate -> predicate.test(tagMap));
  }

  private static Predicate<TagMap> createPredicate(String key, String value) {
    switch (value) {
      case "*":
        return tagMap -> tagMap.get(key).isPresent();
      case "!":
        return tagMap -> tagMap.get(key).isEmpty();
      default:
        if (value.startsWith("!")) return createPredicate(key, value.substring(1)).negate();
        return tagMap -> tagMap.get(key).filter(tag -> value.equals(tag.value())).isPresent();
    }
  }

  public static Selector empty() {
    return new SelectorImpl(Collections.emptyList());
  }

}

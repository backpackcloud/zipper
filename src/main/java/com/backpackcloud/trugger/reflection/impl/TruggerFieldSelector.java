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
package com.backpackcloud.trugger.reflection.impl;

import com.backpackcloud.trugger.reflection.FieldSelector;
import com.backpackcloud.trugger.reflection.ReflectedField;
import com.backpackcloud.trugger.util.ClassIterator;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static com.backpackcloud.trugger.util.Utils.alwaysTrue;

public class TruggerFieldSelector implements FieldSelector {

  private final String name;
  private final Predicate<? super Field> predicate;

  public TruggerFieldSelector(String name) {
    this.name = name;
    this.predicate = alwaysTrue();
  }

  public TruggerFieldSelector(String name, Predicate<? super Field> predicate) {
    this.name = name;
    this.predicate = predicate;
  }

  public FieldSelector filter(Predicate predicate) {
    return new TruggerFieldSelector(this.name, this.predicate.and(predicate));
  }

  public Optional<ReflectedField> from(Object target) {
    return new ClassIterator(target)
      .stream()
      .map(type -> {
        try {
          return type.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
          return null;
        }
      })
      .filter(Objects::nonNull)
      .filter(predicate)
      .findFirst()
      .map(field -> new TruggerReflectedField(field, target));
  }

}

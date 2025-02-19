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

package com.backpackcloud.factory;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class DefaultContext implements Context {

  private final List<Entry> entries;
  private Function<Parameter, Object> defaultFunction = parameter -> null;

  public DefaultContext() {
    this.entries = new ArrayList<>();
  }

  @Override
  public Mapper when(Predicate<? super Parameter> condition) {
    return function -> {
      entries.add(new Entry(function, condition));
      return DefaultContext.this;
    };
  }

  @Override
  public Mapper orElse() {
    return function -> {
      defaultFunction = function;
      
      return DefaultContext.this;
    };
  }

  @Override
  public Optional<Object> resolve(Parameter parameter) {
    for (Entry entry : entries) {
      if (entry.predicate.test(parameter)) {
        return Optional.ofNullable(entry.function.apply(parameter));
      }
    }
    return Optional.ofNullable(defaultFunction.apply(parameter));
  }

  private static class Entry {

    private final Function<Parameter, Object> function;
    private final Predicate<? super Parameter> predicate;

    private Entry(Function<Parameter, Object> supplier,
                  Predicate<? super Parameter> predicate) {
      this.function = supplier;
      this.predicate = predicate;
    }

  }

}

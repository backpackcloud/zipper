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
package com.backpackcloud.reflection.impl;

import com.backpackcloud.reflection.ConstructorsSelector;
import com.backpackcloud.reflection.ReflectedConstructor;
import com.backpackcloud.reflection.Reflection;

import java.lang.reflect.Constructor;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class TruggerConstructorsSelector implements ConstructorsSelector {

  private final Predicate<? super Constructor<?>> predicate;

  public TruggerConstructorsSelector() {
    this(o -> true);
  }

  public TruggerConstructorsSelector(Predicate<? super Constructor<?>> predicate) {
    this.predicate = predicate;
  }

  public ConstructorsSelector filter(Predicate predicate) {
    return new TruggerConstructorsSelector(this.predicate.and(predicate));
  }

  public Stream<ReflectedConstructor> from(Object target) {
    return Stream.of(Reflection.typeOf(target).getDeclaredConstructors())
      .filter(predicate)
      .map(TruggerReflectedConstructor::new);
  }

}

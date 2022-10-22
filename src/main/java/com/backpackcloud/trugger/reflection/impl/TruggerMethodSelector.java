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

import com.backpackcloud.trugger.reflection.MethodSelector;
import com.backpackcloud.trugger.reflection.ReflectedMethod;
import com.backpackcloud.trugger.reflection.ReflectionPredicates;
import com.backpackcloud.trugger.util.Utils;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.backpackcloud.trugger.util.Utils.alwaysTrue;

public class TruggerMethodSelector implements MethodSelector {

  private final String name;
  private final Class[] parameterTypes;
  private final Predicate<? super Method> predicate;

  public TruggerMethodSelector(String name) {
    this.name = name;
    this.parameterTypes = null;
    this.predicate = alwaysTrue();
  }

  public TruggerMethodSelector(String name,
                               Class[] parameterTypes,
                               Predicate<? super Method> predicate) {
    this.name = name;
    this.parameterTypes = parameterTypes;
    this.predicate = predicate;
  }

  public MethodSelector withParameters(Class<?>... parameterTypes) {
    return new TruggerMethodSelector(this.name, parameterTypes, this.predicate);
  }

  public MethodSelector withoutParameters() {
    return withParameters();
  }

  @Override
  public MethodSelector filter(Predicate predicate) {
    return new TruggerMethodSelector(name, parameterTypes, this.predicate.and(predicate));
  }

  public Optional<ReflectedMethod> from(Object target) {
    if (parameterTypes != null) {
      try {
        return Optional.of(Utils.resolveType(target).getDeclaredMethod(name, parameterTypes))
          .filter(predicate)
          .map(method -> new TruggerReflectedMethod(method, target));
      } catch (NoSuchMethodException e) {
        return Optional.empty();
      }
    }

    return Stream.of(Utils.resolveType(target).getDeclaredMethods())
      .filter(ReflectionPredicates.ofName(name))
      .filter(predicate)
      .findFirst()
      .map(method -> new TruggerReflectedMethod(method, target));
  }

}

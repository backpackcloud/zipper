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

import com.backpackcloud.trugger.reflection.ConstructorSelector;
import com.backpackcloud.trugger.reflection.ReflectedConstructor;
import com.backpackcloud.trugger.reflection.ReflectionException;
import com.backpackcloud.trugger.util.Utils;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.backpackcloud.trugger.util.Utils.alwaysTrue;

public class TruggerConstructorSelector implements ConstructorSelector {

  private final Predicate<? super Constructor<?>> predicate;
  private final Class[] parameterTypes;

  public TruggerConstructorSelector() {
    this(alwaysTrue(), null);
  }

  public TruggerConstructorSelector(Predicate<? super Constructor<?>> predicate,
                                    Class[] parameterTypes) {
    this.predicate = predicate;
    this.parameterTypes = parameterTypes;
  }

  public ConstructorSelector filter(Predicate predicate) {
    return new TruggerConstructorSelector(this.predicate.and(predicate), parameterTypes);
  }

  public ConstructorSelector withParameters(Class<?>... parameterTypes) {
    return new TruggerConstructorSelector(predicate, parameterTypes);
  }

  public ConstructorSelector withoutParameters() {
    return withParameters();
  }

  @Override
  public Optional<ReflectedConstructor> from(Object target) throws ReflectionException {
    if (parameterTypes != null) {
      try {
        return Optional.of(Utils.resolveType(target).getDeclaredConstructor(parameterTypes))
          .filter(predicate)
          .map(TruggerReflectedConstructor::new);
      } catch (NoSuchMethodException e) {
        return Optional.empty();
      }
    }
    return Stream.of(Utils.resolveType(target).getDeclaredConstructors())
      .filter(predicate)
      .findFirst()
      .map(TruggerReflectedConstructor::new);
  }

}

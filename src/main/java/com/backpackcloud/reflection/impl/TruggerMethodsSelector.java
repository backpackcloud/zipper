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

import com.backpackcloud.reflection.MethodsSelector;
import com.backpackcloud.reflection.ReflectedMethod;
import com.backpackcloud.reflection.ClassIterator;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TruggerMethodsSelector implements MethodsSelector {

  private final Predicate<? super Method> predicate;

  public TruggerMethodsSelector() {
    this(o -> true);
  }

  public TruggerMethodsSelector(Predicate<? super Method> predicate) {
    this.predicate = predicate;
  }

  @Override
  public MethodsSelector filter(Predicate predicate) {
    return new TruggerMethodsSelector(this.predicate.and(predicate));
  }

  @Override
  public Stream<ReflectedMethod> from(Object target) {
    return new ClassIterator(target)
      .stream()
      .flatMap(type -> Stream.of(type.getDeclaredMethods()))
      .filter(predicate)
      .map(method -> new TruggerReflectedMethod(method, target));
  }

}

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
package com.backpackcloud.trugger.util;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

public final class Utils {

  private Utils() {

  }

  /**
   * Resolves the target type.
   * <p>
   * If the target is a <code>Class</code>, it will be returned. If it is an
   * {@link Annotation}, its {@link Annotation#annotationType() type} will be
   * returned.
   * <p>
   * If none of the above, its {@link Object#getClass() class} will be returned.
   *
   * @param target the target to resolve the type
   * @return the type of the given target.
   */
  public static Class<?> resolveType(Object target) {
    if (target instanceof Annotation) {
      return ((Annotation) target).annotationType();
    }
    return target instanceof Class ? ((Class<?>) target) : target.getClass();
  }

  public static Predicate ALWAYS_TRUE = new Predicate() {

    @Override
    public boolean test(Object o) {
      return true;
    }

    @Override
    public Predicate and(Predicate other) {
      return other;
    }

    @Override
    public Predicate or(Predicate other) {
      return other;
    }

  };

  public static <T> Predicate<T> alwaysTrue() {
    return ALWAYS_TRUE;
  }

}

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

package com.backpackcloud.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.function.Predicate;

/**
 * A class that holds a set of predicates to evaluate parameters.
 */
public final class ParameterPredicates {

  private ParameterPredicates() {

  }

  /**
   * Returns a predicate that accepts parameters of the given type.
   */
  public static Predicate<Parameter> ofType(Class type) {
    return parameter -> type.isAssignableFrom(parameter.getType());
  }

  /**
   * Returns a predicate that accepts parameters with the given name.
   * <p>
   * Note that the code must be compiled with <code>-parameters</code> or
   * the parameter names will be in a argX format.
   */
  public static Predicate<Parameter> ofName(String name) {
    return parameter -> parameter.getName().equals(name);
  }

  /**
   * Returns a predicate that accepts parameters annotated with the given
   * annotation type.
   */
  public static Predicate<Parameter> annotatedWith(Class<? extends Annotation> type) {
    return parameter -> parameter.isAnnotationPresent(type);
  }

}

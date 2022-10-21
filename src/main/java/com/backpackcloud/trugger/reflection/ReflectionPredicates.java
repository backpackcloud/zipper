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
package com.backpackcloud.trugger.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * A utility class for helping the use of {@link Predicate} object that involves
 * Reflection in general.
 *
 * @author Marcelo Guimaraes
 */
public final class ReflectionPredicates {

  private ReflectionPredicates() {
  }

  /**
   * @return a predicate that returns <code>true</code> if the evaluated element is
   * annotated with the specified Annotation.
   */
  public static <T extends AnnotatedElement> Predicate<T> annotatedWith(Class<? extends Annotation> annotationType) {
    return element -> element.isAnnotationPresent(annotationType);
  }

  /**
   * A predicate that returns <code>true</code> if the element has annotations.
   */
  public static Predicate<AnnotatedElement> annotated() {
    return element -> element.getDeclaredAnnotations().length > 0;
  }

  /**
   * @return a predicate that returns <code>true</code> if the evaluated element has a
   * name that with the given one.
   */
  public static <T extends Member> Predicate<T> ofName(String name) {
    return element -> element.getName().equals(name);
  }

  /**
   * @return a predicate that returns <code>true</code> if the evaluated element has the
   * specified modifiers.
   */
  public static <T extends Member> Predicate<T> declared(int... modifiers) {
    return element -> {
      int elModifiers = element.getModifiers();
      for (int mod : modifiers) {
        if ((elModifiers & mod) != 0) {
          return true;
        }
      }
      return false;
    };
  }

  /**
   * Returns a predicate that evaluates to <code>true</code> if the parameter
   * types of a method equals the given types.
   *
   * @param parameterTypes the parameter types
   * @return a predicate to evaluate the parameter types.
   */
  public static Predicate<Method> withParameters(Class... parameterTypes) {
    return method -> Arrays.equals(method.getParameterTypes(), parameterTypes);
  }

  /**
   * Returns a predicate that evaluates to <code>true</code> if a method takes
   * no parameter.
   *
   * @return a predicate to evaluate the method.
   */
  public static Predicate<Method> withoutParameters() {
    return method -> method.getParameterTypes().length == 0;
  }

  /**
   * @return a predicate that returns <code>true</code> if the evaluated method
   * has the specified type as the return type.
   */
  public static Predicate<Method> returning(Class returnType) {
    return method -> method.getReturnType().equals(returnType);
  }

}

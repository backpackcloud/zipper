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
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

/**
 * A set of predicates to use with <code>Class</code> objects.
 *
 * @author Marcelo Guimaraes
 */
public final class ClassPredicates {

  private ClassPredicates() {

  }

  /**
   * Returns a predicate that checks if a class is of the given type.
   */
  public static Predicate<Class> ofType(Class type) {
    return c -> c.equals(type);
  }

  public static Predicate<Class> arrayOf(Class arrayType) {
    return arrayType().and(type -> ofType(arrayType).test(type.getComponentType()));
  }

  /**
   * Returns a predicate that checks if a class is a subtype of another class
   */
  public static Predicate<Class> subtypeOf(Class type) {
    return c -> !c.equals(type) && type.isAssignableFrom(c);
  }

  /**
   * Predicate that returns <code>true</code> if a class is an <i>interface</i>
   * and is not an <i>annotation</i>.
   */
  public static Predicate<Class> interfaceType() {
    return declared(Modifier.INTERFACE).and(assignableTo(Annotation.class).negate());
  }

  /**
   * Predicate that returns <code>true</code> if a class is an <i>enum</i>.
   */
  public static Predicate<Class> enumType() {
    return Class::isEnum;
  }

  /**
   * Predicate that returns <code>true</code> if a class is an <i>annotation</i>.
   */
  public static Predicate<Class> annotationType() {
    return declared(Modifier.INTERFACE).and(assignableTo(Annotation.class));
  }

  /**
   * Predicate that returns <code>true</code> if a class has the class keyword in its declaration.
   */
  public static Predicate<Class> classType() {
    return interfaceType().or(enumType()).or(annotationType()).negate();
  }

  /**
   * A predicate that checks if the given element is an array.
   */
  public static Predicate<Class> arrayType() {
    return Class::isArray;
  }

  /**
   * @return a predicate that returns <code>false</code> if the evaluated class has the
   * specified modifiers.
   */
  public static Predicate<Class> declared(final int... modifiers) {
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
   * @return a predicate that returns <code>true</code> if the specified Class is
   * assignable from the evaluated element.
   */
  public static Predicate<Class> assignableTo(Class clazz) {
    return element -> clazz.isAssignableFrom(element);
  }

}

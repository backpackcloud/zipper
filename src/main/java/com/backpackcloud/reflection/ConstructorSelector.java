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

import java.lang.reflect.Constructor;
import java.util.Optional;

/**
 * Interface that defines a selector for a single {@link Constructor} object.
 *
 * @author Marcelo Guimaraes
 */
public interface ConstructorSelector extends Selector<Constructor<?>, ConstructorSelector> {

  /**
   * Selects the constructor that takes the specified parameter types.
   *
   * @param parameterTypes the parameter types taken by the constructor.
   * @return the component used for selection on the target.
   */
  ConstructorSelector withParameters(Class<?>... parameterTypes);

  /**
   * Selects the constructor that takes no parameters.
   *
   * @return the component used for selection on the target.
   */
  ConstructorSelector withoutParameters();

  /**
   * Selects the single constructor matching the previously specified selectors.
   * <p>
   * This method may throw a {@link ReflectionException} if the specified selectors
   * doesn't take to a single constructor in the given target.
   */
  Optional<ReflectedConstructor> from(Object target) throws ReflectionException;

}

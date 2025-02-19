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

/**
 * Interface that defines a class that encapsulates the reflection using a DSL to
 * create a more readable statement.
 *
 * @author Marcelo Guimaraes
 */
public interface Reflector {

  /**
   * Reflects all fields in a target.
   * <p>
   * Use this method for selecting a set of fields.
   *
   * @return the component used for selection.
   */
  FieldsSelector fields();

  /**
   * Reflects a field with the specified name in a target.
   * <p>
   * Use this method for selecting a single field.
   *
   * @param name the field name.
   * @return the component used for selection.
   */
  FieldSelector field(String name);

  /**
   * Reflects all methods in a target.
   * <p>
   * Use this method for selecting a set of methods.
   *
   * @return the component used for selection.
   */
  MethodsSelector methods();

  /**
   * Reflects a method with the specified name and parameters in a target.
   * <p>
   * Use this method for selecting a single method.
   * <p>
   * <i>The method parameters in question must be informed in the returned object.</i>
   *
   * @param name the method name.
   * @return the component used for selection.
   */
  MethodSelector method(String name);

  /**
   * Reflects a constructor with the specified parameters in a target.
   * <p>
   * Use this method for selecting a single constructor.
   * <p>
   * <i>The constructor parameters in question must be informed in the returned
   * object.</i>
   *
   * @return the component used for selection.
   */
  ConstructorSelector constructor();

  /**
   * Reflects all the constructors.
   * <p>
   * Use this method for selecting a set of constructors.
   *
   * @return the component used for selection.
   */
  ConstructorsSelector constructors();

}

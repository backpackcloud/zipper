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

import com.backpackcloud.reflection.ConstructorSelector;
import com.backpackcloud.reflection.ConstructorsSelector;
import com.backpackcloud.reflection.FieldSelector;
import com.backpackcloud.reflection.FieldsSelector;
import com.backpackcloud.reflection.MethodSelector;
import com.backpackcloud.reflection.MethodsSelector;
import com.backpackcloud.reflection.Reflector;

public class TruggerReflector implements Reflector {

  @Override
  public ConstructorSelector constructor() {
    return new TruggerConstructorSelector();
  }

  @Override
  public ConstructorsSelector constructors() {
    return new TruggerConstructorsSelector();
  }

  @Override
  public FieldSelector field(String name) {
    return new TruggerFieldSelector(name);
  }

  @Override
  public FieldsSelector fields() {
    return new TruggerFieldsSelector();
  }

  @Override
  public MethodSelector method(String name) {
    return new TruggerMethodSelector(name);
  }

  @Override
  public MethodsSelector methods() {
    return new TruggerMethodsSelector();
  }

}

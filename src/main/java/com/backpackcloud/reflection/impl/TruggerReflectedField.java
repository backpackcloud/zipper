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

import com.backpackcloud.reflection.ReflectedField;
import com.backpackcloud.reflection.ReflectionException;

import java.lang.reflect.Field;

public class TruggerReflectedField extends TruggerReflectedObject<Field> implements ReflectedField {

  private final Field field;
  private final Object target;

  public TruggerReflectedField(Field field, Object target) {
    super(field);
    this.field = field;
    this.target = target;
    field.trySetAccessible();
  }

  @Override
  public <E> E get() {
    try {
      return (E) field.get(target);
    } catch (IllegalAccessException e) {
      throw new ReflectionException(e);
    }
  }

  @Override
  public void set(Object newValue) {
    try {
      field.set(target, newValue);
    } catch (Exception e) {
      throw new ReflectionException(e);
    }
  }

  @Override
  public Field unwrap() {
    return this.field;
  }

  @Override
  public Object target() {
    return target;
  }

}

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

import com.backpackcloud.trugger.reflection.ReflectedConstructor;
import com.backpackcloud.trugger.reflection.ReflectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TruggerReflectedConstructor extends TruggerReflectedObject<Constructor<?>> implements ReflectedConstructor {

  private final Constructor<?> constructor;

  public TruggerReflectedConstructor(Constructor<?> constructor) {
    super(constructor);
    this.constructor = constructor;
    constructor.setAccessible(true);
  }

  @Override
  public Object target() {
    return constructor.getDeclaringClass();
  }

  @Override
  public Constructor<?> unwrap() {
    return this.constructor;
  }

  @Override
  public <E> E invoke(Object... args) {
    try {
      return (E) constructor.newInstance(args);
    } catch (InvocationTargetException e) {
      throw new ReflectionException(e.getCause());
    } catch (Exception e) {
      throw new ReflectionException(e);
    }
  }

}

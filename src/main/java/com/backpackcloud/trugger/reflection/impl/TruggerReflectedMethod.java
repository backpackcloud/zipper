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

import com.backpackcloud.trugger.reflection.ReflectedMethod;
import com.backpackcloud.trugger.reflection.ReflectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class TruggerReflectedMethod extends TruggerReflectedObject<Method> implements ReflectedMethod {

  private final Method method;
  private final Object target;

  public TruggerReflectedMethod(Method method, Object target) {
    super(method);
    this.method = method;
    this.target = target;
    method.trySetAccessible();
  }

  @Override
  public Object target() {
    return this.target;
  }

  @Override
  public Method unwrap() {
    return this.method;
  }

  @Override
  public <E> E invoke(Object... args) {
    try {
      return (E) method.invoke(target, args);
    } catch (InvocationTargetException e) {
      throw new ReflectionException(e.getCause());
    } catch (Exception e) {
      throw new ReflectionException(e);
    }
  }

  @Override
  public <E> E invoke(Function<Throwable, E> exceptionHandler, Object... args) {
    try {
      return (E) method.invoke(target, args);
    } catch (InvocationTargetException e) {
      return exceptionHandler.apply(e.getCause());
    } catch (Exception e) {
      throw new ReflectionException(e);
    }
  }

}

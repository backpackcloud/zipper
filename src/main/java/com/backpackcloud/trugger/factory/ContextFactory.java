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

package com.backpackcloud.trugger.factory;

import com.backpackcloud.trugger.reflection.ReflectedConstructor;
import com.backpackcloud.trugger.reflection.Reflection;
import com.backpackcloud.trugger.reflection.ReflectionException;
import com.backpackcloud.trugger.reflection.ReflectionPredicates;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A class that can create objects based on a {@link Context}.
 */
public class ContextFactory {
  private final Context context;

  /**
   * Creates a new instance using a default context implementation
   */
  public ContextFactory() {
    this(new DefaultContext());
  }

  /**
   * Creates a new instance using the given context implementation.
   *
   * @param context the context to use
   */
  public ContextFactory(Context context) {
    this.context = context;
  }

  /**
   * Returns the context used by this factory.
   *
   * @return the context used by this factory.
   */
  public Context context() {
    return context;
  }

  /**
   * Creates a new instance of the given type by looping through its public
   * constructors to find one which all parameters are resolved by the context.
   *
   * @param type the type of the object to create.
   * @return the created object
   */
  public <E> Optional<E> create(Class<E> type) {
    List<ReflectedConstructor> constructors = Reflection.reflect().constructors()
      .filter(ReflectionPredicates.declared(Modifier.PUBLIC))
      .from(type)
      .sorted((o1, o2) -> o2.unwrap().getParameterCount() - o1.unwrap().getParameterCount())
      .toList();

    Optional<E> created;
    for (ReflectedConstructor constructor : constructors) {
      try {
        created = tryCreate(constructor);
      } catch (ReflectionException e) {
        throw new ReflectionException("Error while creating a " + type, e.getCause());
      }
      if (created.isPresent()) {
        return created;
      }
    }
    return Optional.empty();
  }

  public Object[] resolveArgs(Executable executable) {
    Object[] args = new Object[executable.getParameterCount()];
    Object arg;
    int i = 0;
    for (Parameter parameter : executable.getParameters()) {
      arg = context.resolve(parameter).orElse(null);
      args[i++] = arg;
    }
    return args;
  }

  // tries to create the object using the given constructor
  private Optional tryCreate(ReflectedConstructor constructor) {
    Object[] args = resolveArgs(constructor.unwrap());
    return Optional.ofNullable(Arrays.stream(args).allMatch(Objects::nonNull) ? constructor.invoke(args) : null);
  }

}

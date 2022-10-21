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

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.backpackcloud.trugger.reflection.ParameterPredicates.annotatedWith;
import static com.backpackcloud.trugger.reflection.ParameterPredicates.ofType;

/**
 * Interface that defines a context to create a object. A context indicates
 * which object can be used as an argument to invoke a constructor.
 */
public interface Context {

  Mapper when(Predicate<? super Parameter> condition);

  Mapper orElse();

  default Mapper asDefault() {
    return orElse();
  }

  default Mapper whenOfType(Class<?> type) {
    return when(ofType(type));
  }

  default Mapper whenAnnotatedWith(Class<? extends Annotation> annotationType) {
    return when(annotatedWith(annotationType));
  }

  /**
   * Tries to resolve the given parameter to a object using the predicates
   * added to the context.
   *
   * @param parameter the parameter to resolve the value
   * @return the resolved value.
   */
  Optional<Object> resolve(Parameter parameter);

  interface Mapper {

    default Context use(Object object) {
      return use((parameter) -> object);
    }

    default Context use(Supplier supplier) {
      return use(parameter -> supplier.get());
    }

    Context use(Function<Parameter, Object> function);

  }

}

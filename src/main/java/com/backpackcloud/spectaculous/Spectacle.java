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

package com.backpackcloud.spectaculous;

import org.hamcrest.Matcher;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Interface that allows the definition of the spec statements.
 *
 * @param <T> the type of the target object
 */
public interface Spectacle<T> {

  /**
   * Replaces the target object with the given one.
   *
   * @param object the new target object
   * @return a new Spectacle that uses the given target object.
   */
  default Spectacle<T> given(T object) {
    return given(() -> object);
  }

  /**
   * Replaces the target object with the one return by the given supplier.
   *
   * @param supplier the new supplier for the target object.
   * @return a new Spectacle that uses the given supplier.
   */
  Spectacle<T> given(Supplier<T> supplier);

  /**
   * Adds a reason to the following statements. The reason will be used to
   * compose the exception text in case of failure to follow the spec.
   *
   * @param reason the reason for the following statements.
   * @return a new Spectacle that uses the given reason
   */
  Spectacle<T> because(String reason);

  StatementActionDefinition<T> when(TargetedAction<? super T> action);

  StatementActionDefinition<T> when(Action action);

  <R> StatementOperationDefinition<T, R> from(Operation<? super T, R> operation);

  /**
   * Defines an action statement that takes the target object.
   *
   * @param action the action to run
   * @return a component for defining the outcome
   */
  Spectacle<T> then(TargetedAction<? super T> action);

  /**
   * Defines an action statement that doesn't require the target object.
   *
   * @param action the action to run
   * @return a component for defining the outcome
   */
  Spectacle<T> then(Action action);

  /**
   * Interface for defining the action of a statement.
   */
  interface StatementActionDefinition<T> {

    Spectacle<T> expect(Class<? extends Throwable> throwable);

    default Spectacle<T> expectFailure() {
      return expect(Throwable.class);
    }

  }

  /**
   * Interface for defining the operation of a statement
   */
  interface StatementOperationDefinition<T, R> {

    /**
     * Starts a statement that defines a predicate that should be tested.
     *
     * @param predicate the predicate to test
     * @return a component for defining the target of the test.
     */
    Spectacle<T> expect(Predicate<? super R> predicate);

    /**
     * Starts a statement that defines a value that should be expected.
     *
     * @param supplier the supplier for the value
     * @return a component for defining the target to test.
     */
    Spectacle<T> expect(Supplier<R> supplier);

    /**
     * Starts a statement that defines a Hamcrest Matcher to test.
     *
     * @param matcher the matcher to test
     * @return a component for defining the target to test.
     */
    default Spectacle<T> expect(Matcher<? super R> matcher) {
      return expect(matcher::matches);
    }

    /**
     * Starts a statement that defines a value that is expected.
     *
     * @param value the expected value
     * @return a component for defining the target to test.
     */
    default Spectacle<T> expect(R value) {
      return expect(() -> value);
    }

  }
}

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

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Initial class that offers a start point for defining specs.
 */
public final class Backstage<T> implements Spectacle<T> {
  private final Supplier<? extends T> supplier;
  private final String reason;
  private final String scenario;

  Backstage(String scenario) {
    this(scenario, () -> {
      throw new SpectacularException("No object given");
    }, "");
  }

  private Backstage(String scenario, Supplier<? extends T> supplier, String reason) {
    this.scenario = scenario;
    this.supplier = supplier;
    this.reason = reason;
  }

  private <E> Spectacle<T> test(E target, Predicate<? super E> predicate) {
    if (!predicate.test(target)) {
      throwSpecException(null);
    }
    return this;
  }

  private <E> Spectacle<T> test(E target, Predicate<? super E> predicate, Supplier<String> failMessage) {
    if (!predicate.test(target)) {
      throwSpecException(null, failMessage);
    }
    return this;
  }

  private <E> E throwSpecException() {
    return throwSpecException(null);
  }

  private <E> E throwSpecException(Throwable cause) {
    if (cause instanceof SpectacularException) {
      throw (SpectacularException) cause;
    }
    String message = reason.isEmpty() ? scenario : String.format("%s: %s", scenario, reason);
    throw new SpectacularException(message, cause);
  }

  private <E> E throwSpecException(Throwable cause, Supplier<String> failMessage) {
    if (cause instanceof SpectacularException) {
      throw (SpectacularException) cause;
    }
    String message = reason.isEmpty() ?
      String.format("%s [%s]", scenario, failMessage.get()) :
      String.format("%s: %s [%s]", scenario, reason, failMessage.get());
    throw new SpectacularException(message, cause);
  }

  @Override
  public Spectacle<T> then(TargetedAction<? super T> action) {
    try {
      action.run(supplier.get());
    } catch (Throwable throwable) {
      throwSpecException(throwable);
    }
    return this;
  }

  @Override
  public Spectacle<T> then(Action action) {
    try {
      action.run();
    } catch (Throwable throwable) {
      throwSpecException(throwable);
    }
    return this;
  }

  @Override
  public Spectacle<T> given(Supplier<T> supplier) {
    return new Backstage<>(scenario, supplier, reason);
  }

  @Override
  public Spectacle<T> because(String newReason) {
    return new Backstage<>(scenario, supplier, newReason);
  }

  @Override
  public StatementActionDefinition<T> when(Action action) {
    return throwable -> {
      try {
        action.run();
        throwSpecException();
      } catch (Throwable e) {
        test(e.getClass(), throwable::isAssignableFrom);
      }
      return Backstage.this;
    };
  }

  @Override
  public StatementActionDefinition<T> when(TargetedAction<? super T> action) {
    return throwable -> {
      try {
        action.run(supplier.get());
        throwSpecException();
      } catch (Throwable e) {
        test(e.getClass(), throwable::isAssignableFrom);
      }
      return Backstage.this;
    };
  }

  @Override
  public <R> StatementOperationDefinition<T, R> from(Operation<? super T, R> operation) {
    return new StatementOperationDefinition<>() {
      @Override
      public Spectacle<T> expect(Predicate<? super R> predicate) {
        try {
          return test(operation.execute(supplier.get()), predicate);
        } catch (Throwable throwable) {
          return throwSpecException(throwable);
        }
      }

      @Override
      public Spectacle<T> expect(Supplier<R> supplier) {
        try {
          R expectedValue = supplier.get();
          R actualValue = operation.execute(Backstage.this.supplier.get());
          return test(actualValue, r -> Objects.equals(r, expectedValue),
            () -> String.format("%s != %s", expectedValue, actualValue));
        } catch (Throwable throwable) {
          return throwSpecException(throwable);
        }
      }
    };
  }

  /**
   * Starts a new spec describing it with the given scenario.
   *
   * @param scenario the given scenario that describes this spec.
   * @return a new Spec
   */
  public static Backstage describe(String scenario) {
    return new Backstage(scenario);
  }

  /**
   * Starts a new spec describing it with the given class.
   *
   * @param type the class that is the target of this spec.
   * @return a new Spec
   */
  public static <T> Backstage<T> describe(Class<T> type) {
    return describe(type.getSimpleName());
  }

}
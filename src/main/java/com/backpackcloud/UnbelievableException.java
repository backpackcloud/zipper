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

package com.backpackcloud;

import java.util.function.Supplier;

/**
 * OMG! I can't believe this is happening!
 *
 * @author Marcelo Guimarães
 */
public class UnbelievableException extends RuntimeException {

  public UnbelievableException() {
  }

  public UnbelievableException(String message) {
    super(message);
  }

  public UnbelievableException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnbelievableException(Throwable cause) {
    super(cause);
  }

  public UnbelievableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * Creates a supplier that uses the given reason as the exception message.
   *
   * @param reason the exception message
   * @return a supplier that creates a new UnbelievableException with the given reason.
   */
  public static Supplier<UnbelievableException> because(String reason) {
    return () -> new UnbelievableException(reason);
  }

}

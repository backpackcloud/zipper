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

package com.backpackcloud.cli;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class ErrorRegistry implements Registry {

  private final List<Exception> errors;
  private boolean viewed;

  public ErrorRegistry() {
    this(new ArrayList<>());
  }

  public ErrorRegistry(List<Exception> errors) {
    this.errors = errors;
    this.viewed = true;
  }

  public boolean viewed() {
    return viewed;
  }

  public void add(Exception exception) {
    viewed = false;
    errors.add(exception);
  }

  @Override
  public void clear() {
    viewed = true;
    errors.clear();
  }

  @Override
  public boolean isEmpty() {
    return errors.isEmpty();
  }

  @Override
  public int size() {
    return errors.size();
  }

  public Stream<Exception> stream() {
    viewed = true;
    return errors.stream();
  }

  public String name() {
    return "errors";
  }

}

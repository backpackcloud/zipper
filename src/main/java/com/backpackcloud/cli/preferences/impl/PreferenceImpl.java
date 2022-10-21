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

package com.backpackcloud.cli.preferences.impl;

import com.backpackcloud.cli.preferences.Preference;
import com.backpackcloud.cli.preferences.PreferenceSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PreferenceImpl<E> implements Preference<E> {

  private final PreferenceSpec spec;
  private final List<Consumer<E>> listeners;
  private E currentValue;
  private String currentInputValue;

  public PreferenceImpl(PreferenceSpec spec) {
    this.spec = spec;
    this.listeners = new ArrayList<>();
    reset();
  }

  @Override
  public PreferenceSpec spec() {
    return spec;
  }

  @Override
  public E value() {
    return currentValue;
  }

  @Override
  public String inputValue() {
    return currentInputValue;
  }

  @Override
  public void set(String input) {
    this.currentValue = (E) spec().type().convert(input);
    this.currentInputValue = input;
    this.listeners.forEach(listener -> listener.accept(this.currentValue));
  }

  @Override
  public void reset() {
    set(spec().defaultValue());
  }

  @Override
  public void listen(Consumer<E> listener) {
    listener.accept(this.currentValue);
    this.listeners.add(listener);
  }

}

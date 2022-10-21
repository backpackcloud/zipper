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

package com.backpackcloud.cli.preferences;

import java.util.function.Consumer;

/**
 * Defines a preference that users can set values via
 * the interactive console.
 *
 * @param <E>
 * @author Marcelo "Ataxexe" Guimarães
 */
public interface Preference<E> {

  /**
   * @return the spec for this preference.
   */
  PreferenceSpec spec();

  /**
   * @return the current value this preference is holding.
   */
  E value();

  /**
   * @return the input String which generated the current {@link #value()}.
   */
  String inputValue();

  /**
   * Changes the value by supplying an input String.
   *
   * @param input the input string for producing the value.
   * @see PreferenceSpec.Type#convert(String)
   */
  void set(String input);

  /**
   * Reverts the value back to its
   * {@link PreferenceSpec#defaultValue()  default} one.
   */
  void reset();

  /**
   * Listens to any value change for this preference.
   * <p>
   * In case the value gets changed or cleared, the given
   * listener will be notified immediately in the same
   * Thread.
   * <p>
   * The listener will be notified about the current value
   * of the property as soon as it's registered, so there is
   * no need for getting the current value prior to listen
   * to changes.
   *
   * @param listener the listener to notify on value change
   */
  void listen(Consumer<E> listener);

}

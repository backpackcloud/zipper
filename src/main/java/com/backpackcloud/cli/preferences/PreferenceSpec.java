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

import com.backpackcloud.UnbelievableException;

/**
 * Represents a preference's specification.
 * <p>
 * The specification basically tells information about
 * the preference and how it gets converted into a
 * useful value.
 *
 * @author Marcelo "Ataxexe" Guimarães
 */
public interface PreferenceSpec {

  /**
   * @return the unique identifier for the preference.
   */
  String id();

  /**
   * @return a brief description of the preference.
   */
  String description();

  /**
   * @return the type of the preference.
   */
  Type type();

  /**
   * @return the default value that the preference holds.
   */
  String defaultValue();

  /**
   * Enumerates the possible types of preferences.
   *
   * @author Marcelo "Ataxexe" Guimarães
   */
  enum Type {

    /**
     * A flag preference. It holds a Boolean value and supports "on/off" and "yes/no"
     * in addition to the obvious "true/false" possibility when converting the input.
     */
    FLAG("Flag") {
      @Override
      public Object convert(String input) {
        return switch (input) {
          case "on", "true", "yes" -> true;
          case "off", "false", "no" -> false;
          default -> throw new UnbelievableException("Invalid input!");
        };
      }
    },
    /**
     * A text preference. It holds a String value (no conversion is done).
     */
    TEXT("Text") {
      @Override
      public Object convert(String input) {
        return input;
      }
    },
    /**
     * A number preference. It holds an Integer value.
     */
    NUMBER("Number") {
      @Override
      public Object convert(String input) {
        return Integer.parseInt(input);
      }
    };

    private final String description;

    Type(String description) {
      this.description = description;
    }

    /**
     * @return a brief description of the effects that the preference have in the system.
     */
    public String description() {
      return description;
    }

    /**
     * Converts an input text into a value that this type of preference can hold.
     *
     * @param input the input value to convert.
     * @return the converted value.
     */
    public abstract Object convert(String input);

  }

}

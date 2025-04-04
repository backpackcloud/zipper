/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Marcelo "Ataxexe" Guimarães
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

import com.backpackcloud.text.InputValue;
import org.jline.reader.ParsedLine;

import java.util.List;

/**
 * Represents a command argument.
 *
 * @author Marcelo "Ataxexe" Guimarães
 */
public class CommandInput {

  private final ParsedLine parsedLine;

  public CommandInput(ParsedLine parsedLine) {
    this.parsedLine = parsedLine;
  }

  public InputValue line() {
    return InputValue.of(parsedLine.line());
  }

  public List<InputValue> words() {
    List<String> words = parsedLine.words();
    return words.subList(1, words.size()).stream()
      .map(InputValue::of)
      .toList();
  }

}

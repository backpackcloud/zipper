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

package com.backpackcloud.cli.impl;

import com.backpackcloud.cli.CommandInput;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandInputImpl implements CommandInput {

  private final String value;
  private final List<String> splitValue;

  public CommandInputImpl(String value, List<String> splitValue) {
    this.value = value;
    this.splitValue = splitValue;
  }

  public CommandInputImpl(List<String> splitValue) {
    this.value = String.join(" ", splitValue);
    this.splitValue = splitValue;
  }

  @Override
  public boolean isEmpty() {
    return value.isEmpty();
  }

  @Override
  public List<CommandInput> asList() {
    return splitValue.stream()
      .map(s -> new CommandInputImpl(s, Collections.singletonList(s)))
      .collect(Collectors.toList());
  }

  @Override
  public String asString() {
    return value;
  }

  @Override
  public Optional<Integer> asInt() {
    try {
      return Optional.of(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  @Override
  public Iterator<String> iterator() {
    return Collections.unmodifiableList(splitValue).iterator();
  }

}

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

package com.backpackcloud.cli.ui.impl;

import com.backpackcloud.cli.ui.Suggestion;

import java.util.Optional;

public class PromptSuggestion implements Suggestion {

  private final String value;

  private final String description;

  private final String group;

  private final boolean complete;

  private PromptSuggestion(String value, String description, String group, boolean complete) {
    this.value = value;
    this.description = description;
    this.group = group;
    this.complete = complete;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public Optional<String> description() {
    return Optional.ofNullable(description);
  }

  @Override
  public Optional<String> group() {
    return Optional.ofNullable(group);
  }

  @Override
  public boolean isComplete() {
    return complete;
  }

  public PromptSuggestion incomplete() {
    return new PromptSuggestion(value, description, group, false);
  }

  public PromptSuggestion describedAs(String description) {
    return new PromptSuggestion(this.value, description, this.group, this.complete);
  }

  public PromptSuggestion asPartOf(String group) {
    return new PromptSuggestion(this.value, this.description, group, this.complete);
  }

  public static PromptSuggestion suggest(String value) {
    return new PromptSuggestion(value, null, null, true);
  }

}

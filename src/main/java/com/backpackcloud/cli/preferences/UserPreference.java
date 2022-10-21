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

/**
 * Enumeration of all the user preferences available.
 *
 * @author Marcelo "Ataxexe" Guimarães
 */
public enum UserPreference implements PreferenceSpec {

  COMPLETION(
    "shows command completion as suggestions",
    Type.FLAG,
    "true"
  ),
  AUTO_SUGGEST(
    "suggests as you type",
    Type.FLAG,
    "true"
  ),
  HIGHLIGHTER(
    "highlights commands",
    Type.FLAG,
    "true"
  ),
  RESULT_PAGING(
    "enables result paging when applicable",
    Type.FLAG,
    "true"
  ),

  LEFT_PROMPT_HEAD(
    "The head icon for the left prompt",
    Type.TEXT,
    "head-blurred"
  ),
  LEFT_PROMPT_SEPARATOR(
    "The segment separator icon for the left prompt",
    Type.TEXT,
    "separator-none"
  ),
  LEFT_PROMPT_TAIL(
    "The tail icon for the left prompt",
    Type.TEXT,
    "tail-slanted"
  ),

  RIGHT_PROMPT_HEAD(
    "The head icon for the right prompt",
    Type.TEXT,
    "head-slanted"
  ),
  RIGHT_PROMPT_SEPARATOR(
    "The segment separator icon for the right prompt",
    Type.TEXT,
    "separator-none"
  ),
  RIGHT_PROMPT_TAIL(
    "The tail icon for the right prompt",
    Type.TEXT,
    "tail-blurred"
  ),

  RESULTS_PER_PAGE(
    "sets the results to show per page when paging is enabled",
    Type.NUMBER,
    "25"
  ),

  ;

  private final String id;
  private final String description;
  private final Type type;
  private final String defaultValue;

  UserPreference(String description, Type type, String defaultValue) {
    this.id = name().toLowerCase().replaceAll("_", "-");
    this.description = description;
    this.type = type;
    this.defaultValue = defaultValue;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public String defaultValue() {
    return defaultValue;
  }

}

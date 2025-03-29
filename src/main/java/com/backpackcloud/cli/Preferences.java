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

import com.backpackcloud.preferences.PreferenceSpec;
import com.backpackcloud.preferences.PreferenceType;

/**
 * Enumeration of the base user preferences available.
 *
 * @author Marcelo "Ataxexe" Guimarães
 */
public final class Preferences {

  public static final PreferenceSpec<Boolean> COMPLETION = new PreferenceSpec<>(
    "completion",
    "shows command completion as suggestions",
    PreferenceType.FLAG,
    "true"
  );

  public static final PreferenceSpec<Boolean> AUTO_SUGGEST = new PreferenceSpec<>(
    "auto-suggest",
    "suggests as you type",
    PreferenceType.FLAG,
    "true"
  );

  public static final PreferenceSpec<Boolean> HIGHLIGHTER = new PreferenceSpec<>(
    "highlighter",
    "highlights commands as you type",
    PreferenceType.FLAG,
    "true"
  );

  public static final PreferenceSpec<Boolean> RESULT_PAGING = new PreferenceSpec<>(
    "result-paging",
    "enables result paging when applicable",
    PreferenceType.FLAG,
    "true"
  );

  public static final PreferenceSpec<Integer> RESULTS_PER_PAGE = new PreferenceSpec<>(
    "results-per-page",
    "sets the results to show per page when paging is enabled",
    PreferenceType.NUMBER,
    "25"
  );

  public static final PreferenceSpec<Boolean> CLEAR_ON_PAGING = new PreferenceSpec<>(
    "clear-on-paging",
    "clears the screen before paging actions",
    PreferenceType.FLAG,
    "false"
  );

  public static final PreferenceSpec<String> LEFT_PROMPT_HEAD = new PreferenceSpec<>(
    "left-prompt-head",
    "The head icon for the left prompt",
    PreferenceType.TEXT,
    "head-blurred"
  );

  public static final PreferenceSpec<String> LEFT_PROMPT_SEPARATOR = new PreferenceSpec<>(
    "left-prompt-separator",
    "The segment separator icon for the left prompt",
    PreferenceType.TEXT,
    "separator-none"
  );

  public static final PreferenceSpec<String> LEFT_PROMPT_TAIL = new PreferenceSpec<>(
    "left-prompt-tail",
    "The tail icon for the left prompt",
    PreferenceType.TEXT,
    "tail-slanted"
  );

  public static final PreferenceSpec<String> RIGHT_PROMPT_HEAD = new PreferenceSpec<>(
    "right-prompt-head",
    "The head icon for the right prompt",
    PreferenceType.TEXT,
    "head-slanted"
  );

  public static final PreferenceSpec<String> RIGHT_PROMPT_SEPARATOR = new PreferenceSpec<>(
    "right-prompt-separator",
    "The segment separator icon for the right prompt",
    PreferenceType.TEXT,
    "separator-none"
  );

  public static final PreferenceSpec<String> RIGHT_PROMPT_TAIL = new PreferenceSpec<>(
    "right-prompt-tail",
    "The tail icon for the right prompt",
    PreferenceType.TEXT,
    "tail-blurred"
  );

  private Preferences() {

  }

}

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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileSuggester {

  private String normalize(String path) {
    return path.replaceAll("\\\\", "/");
  }

  private Suggestion createSuggestion(File suggestion) {
    String path = normalize(suggestion.getPath());
    if (suggestion.isDirectory()) {
      return PromptSuggestion.suggest(path + "/").incomplete();
    }
    return PromptSuggestion.suggest(path).incomplete();
  }

  public List<Suggestion> suggest(String input) {
    String path;

    if (input.isBlank()) {
      path = ".";
    } else {
      path = input;
    }

    File file = new File(path);
    List<Suggestion> result = new ArrayList<>();
    if (file.exists()) {
      result.add(PromptSuggestion.suggest(file.getPath()).incomplete());

      if (file.isDirectory()) {
        Arrays.stream(file.listFiles())
          .map(this::createSuggestion)
          .forEach(result::add);
      }
    }

    File parent = file.getParentFile();
    if (parent != null) {
      Arrays.stream(parent.listFiles(p -> p.getPath().startsWith(path)))
        .map(this::createSuggestion)
        .forEach(result::add);
    }

    return result;
  }

}

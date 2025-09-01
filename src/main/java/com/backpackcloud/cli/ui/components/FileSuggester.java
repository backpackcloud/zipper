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

package com.backpackcloud.cli.ui.components;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.ui.Suggestion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileSuggester {

  private String normalize(String path) {
    return path.replaceAll("\\\\", "/");
  }

  private Suggestion createSuggestion(File suggestion) {
    String path = normalize(suggestion.getPath());
    if (suggestion.isDirectory()) {
      return PromptSuggestion.suggest(path)
        .asPartOf("Directories")
        .incomplete();
    }
    return PromptSuggestion.suggest(path)
      .describedAs(String.format("%d bytes", suggestion.length()))
      .asPartOf("Files");
  }

  public List<Suggestion> suggest(String input) {
    try {
      if (input == null || input.isBlank()) {
        return collect(Paths.get(""));
      } else {
        Path path = Paths.get(input);
        File file = path.toFile();
        if (file.exists()) {
          if (file.isDirectory()) {
            List<Suggestion> result = new ArrayList<>(collect(path));
            result.add(createSuggestion(file));
            return result;
          } else {
            Path parent = path.getParent();
            if (parent != null) {
              return collect(parent);
            } else {
              return suggest(null);
            }
          }
        } else {
          Path parent = path.getParent();
          if (parent != null) {
            return collect(parent);
          }
        }
      }
      return Collections.emptyList();
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  private List<Suggestion> collect(Path path) throws IOException {
    return Files.list(path)
      .map(Path::toFile)
      .map(this::createSuggestion)
      .collect(Collectors.toList());
  }

}

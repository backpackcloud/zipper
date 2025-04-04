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

import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandInput;
import com.backpackcloud.cli.Preferences;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.preferences.UserPreferences;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandCompleter implements Completer {

  private final Map<String, Command> commands;
  private final UserPreferences userPreferences;

  public CommandCompleter(Map<String, Command> commands, UserPreferences userPreferences) {
    this.commands = commands;
    this.userPreferences = userPreferences;
  }

  @Override
  public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
    if (userPreferences.isEnabled(Preferences.COMPLETION)) {
      suggest(line).stream()
        .map(Suggestion::toCandidate)
        .forEach(candidates::add);
    }
  }

  private List<Suggestion> suggest(ParsedLine parsedLine) {
    List<Suggestion> suggestions = new ArrayList<>();
    List<String> words = parsedLine.words();
    String firstWord = words.getFirst();

    if (words.size() == 1) {
      commands.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(firstWord))
        .map(entry -> PromptSuggestion
          .suggest(entry.getKey())
          .describedAs(entry.getValue().description())
          .asPartOf(entry.getValue().type()))
        .forEach(suggestions::add);
    } else if (commands.containsKey(firstWord)) {
      suggestions.addAll(commands.get(firstWord).suggest(new CommandInput(parsedLine)));
    }

    return suggestions;
  }

}

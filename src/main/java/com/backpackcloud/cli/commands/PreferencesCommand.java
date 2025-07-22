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

package com.backpackcloud.cli.commands;

import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.annotations.Action;
import com.backpackcloud.cli.annotations.CommandDefinition;
import com.backpackcloud.cli.annotations.InputParameter;
import com.backpackcloud.cli.annotations.ParameterSuggestion;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.components.PromptSuggestion;
import com.backpackcloud.preferences.Preference;
import com.backpackcloud.preferences.UserPreferences;

import java.util.List;
import java.util.stream.Collectors;

@CommandDefinition(
  name = "preferences",
  description = "Manages the user preferences"
)
public class PreferencesCommand {

  private final UserPreferences userPreferences;

  public PreferencesCommand(UserPreferences userPreferences) {
    this.userPreferences = userPreferences;
  }

  @Action
  public void set(@InputParameter Preference<?> preference, @InputParameter String value) {
    preference.set(value);
  }

  @Action
  public void clear(@InputParameter Preference<?> preference) {
    preference.reset();
  }

  @Action
  public void list(Writer writer) {
    userPreferences.list().forEach(preference -> writer
      .withStyle("preference_name")
      .write(preference.spec().id()).write(": ")

      .withStyle("preference_" + preference.spec().type().name().toLowerCase())
      .write(String.valueOf(preference.inputValue().get()))

      .withStyle("preference_description")
      .write(String.format(" (%s)", preference.spec().description()))
      .newLine()
    );
  }

  @ParameterSuggestion(action = "set", parameter = "preference")
  @ParameterSuggestion(action = "clear", parameter = "preference")
  public List<Suggestion> suggestPreferences() {
    return userPreferences.list().stream()
      .map(preferences ->
        PromptSuggestion.suggest(preferences.spec().id())
          .describedAs(String.format(
            "%s - \"%s\"",
            preferences.spec().description(),
            preferences.inputValue()))
          .asPartOf(preferences.spec().type().name())
      )
      .collect(Collectors.toList());
  }

}

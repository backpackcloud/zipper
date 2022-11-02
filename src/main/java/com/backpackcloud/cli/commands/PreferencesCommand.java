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

package com.backpackcloud.cli.commands;

import com.backpackcloud.cli.Action;
import com.backpackcloud.cli.AnnotatedCommand;
import com.backpackcloud.cli.CommandDefinition;
import com.backpackcloud.cli.Suggestions;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.preferences.Preference;
import com.backpackcloud.cli.preferences.UserPreferences;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.impl.PromptSuggestion;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;
@ApplicationScoped
@CommandDefinition(
  name = "preferences",
  description = "Manages the user preferences"
)
@RegisterForReflection
public class PreferencesCommand implements AnnotatedCommand {

  private final UserPreferences userPreferences;

  public PreferencesCommand(UserPreferences userPreferences) {
    this.userPreferences = userPreferences;
  }

  @Action("set")
  public void setPreference(Preference<?> preference, String value) {
    preference.set(value);
  }

  @Action("clear")
  public void clearPreference(Preference<?> preference) {
    preference.reset();
  }

  @Action("list")
  public void listPreferences(Writer writer) {
    userPreferences.list().forEach(preference -> {
      writer.withStyle("preference_name").write(preference.spec().id()).write(": ");
      writer.withStyle("preference_" + preference.spec().type().name().toLowerCase()).write(String.valueOf(preference.inputValue()));
      writer.newLine();
    });
  }

  @Suggestions({"set", "clear"})
  public List<Suggestion> suggestPreferences() {
    return userPreferences.list().stream()
      .map(preferences ->
        PromptSuggestion.suggest(preferences.spec().id())
          .describedAs(String.format(
            "%s - \"%s\"",
            preferences.spec().description(),
            preferences.inputValue()))
          .asPartOf(preferences.spec().type().description())
      )
      .collect(Collectors.toList());
  }

}

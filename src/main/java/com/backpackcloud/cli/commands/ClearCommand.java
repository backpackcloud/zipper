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
import com.backpackcloud.cli.Registry;
import com.backpackcloud.cli.Suggestions;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.impl.PromptSuggestion;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.enterprise.inject.Instance;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CommandDefinition(
  name = "clear",
  description = "Clears the given records or everything"
)
@RegisterForReflection
public class ClearCommand implements AnnotatedCommand {

  private final Map<String, Registry> registries;

  public ClearCommand(Instance<Registry> registries) {
    this.registries = new HashMap<>();

    registries.forEach(registry -> this.registries.put(registry.name(), registry));
  }

  @Action
  public void execute(String... args) {
    if (args.length == 0) {
      registries.values().forEach(Registry::clear);
    } else {
      Stream.of(args)
        .map(registries::get)
        .filter(Objects::nonNull)
        .forEach(Registry::clear);
    }
  }

  @Suggestions
  public List<Suggestion> execute() {
    return registries.keySet().stream()
      .map(PromptSuggestion::suggest)
      .collect(Collectors.toList());
  }

}

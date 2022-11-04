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
import com.backpackcloud.cli.ui.ColorMap;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.impl.PromptSuggestion;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;
@ApplicationScoped
@CommandDefinition(
  name = "color",
  description = "Manages the UI colors"
)
@RegisterForReflection
public class ColorCommand implements AnnotatedCommand {

  private final ColorMap colorMap;

  public ColorCommand(ColorMap colorMap) {
    this.colorMap = colorMap;
  }

  @Action
  public void set(String colorName, String colorValue) {
    colorMap.put(colorName, colorValue);
  }

  @Action
  public void list(Writer writer) {
    colorMap.colorKeys().forEach(color ->
      writer.write(color).write(": ")
        .withStyle(color).writeln(colorMap.valueOf(color)));
  }

  @Suggestions("set")
  public List<Suggestion> suggest() {
    return colorMap.colorKeys().stream()
      .map(color -> PromptSuggestion.suggest(color)
        .describedAs(colorMap.valueOf(color)))
      .collect(Collectors.toList());
  }

}

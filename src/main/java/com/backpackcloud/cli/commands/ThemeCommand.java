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

import com.backpackcloud.cli.annotations.Action;
import com.backpackcloud.cli.annotations.CommandDefinition;
import com.backpackcloud.cli.annotations.Suggestions;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.ui.ColorMap;
import com.backpackcloud.cli.ui.IconMap;
import com.backpackcloud.cli.ui.StyleMap;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.Theme;
import com.backpackcloud.cli.ui.components.PromptSuggestion;

import java.util.List;
import java.util.stream.Collectors;

@CommandDefinition(
  name = "theme",
  description = "Manages the UI theme"
)
public class ThemeCommand {

  private final IconMap iconMap;
  private final ColorMap colorMap;
  private final StyleMap styleMap;

  public ThemeCommand(Theme theme) {
    this.iconMap = theme.iconMap();
    this.colorMap = theme.colorMap();
    this.styleMap = theme.styleMap();
  }

  @Action
  public void style(String styleName, String styleValue) {
    styleMap.put(styleName, styleValue);
  }

  @Suggestions
  public List<Suggestion> style() {
    return styleMap.styles().stream()
      .map(style -> PromptSuggestion.suggest(style)
        .describedAs(styleMap.styleOf(style)))
      .collect(Collectors.toList());
  }

  @Action
  public void styles(Writer writer) {
    styleMap.styles().forEach(name -> {
        String mappedStyle = styleMap.styleOf(name);
        writer.write(name).write(": ")
          .withStyle(mappedStyle).writeln(mappedStyle);
      }
    );
  }

  @Action
  public void color(String colorName, String colorValue) {
    colorMap.put(colorName, colorValue);
  }

  @Suggestions
  public List<Suggestion> color() {
    return colorMap.colors().stream()
      .map(color -> PromptSuggestion.suggest(color)
        .describedAs(colorMap.valueOf(color)))
      .collect(Collectors.toList());
  }

  @Action
  public void colors(Writer writer) {
    colorMap.colors().forEach(color ->
      writer.write(color).write(": ")
        .withStyle(color).writeln(colorMap.valueOf(color)));
  }

  @Action
  public void icon(String iconName, String iconValue) {
    iconMap.put(iconName, iconValue);
  }

  @Suggestions
  public List<Suggestion> icon() {
    return iconMap.icons().stream()
      .map(icon -> PromptSuggestion.suggest(icon)
        .describedAs(iconMap.symbolOf(icon)))
      .collect(Collectors.toList());
  }

  @Action
  public void icons(Writer writer) {
    iconMap.icons().forEach(icon ->
      writer.style().set()
        .write(icon)
        .write(": ")
        .writeIcon(icon)
        .newLine());
  }

}

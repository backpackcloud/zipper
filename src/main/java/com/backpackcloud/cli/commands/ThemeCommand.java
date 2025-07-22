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

  @Action("style")
  public void manageStyle(@InputParameter("name") String styleName,
                          @InputParameter("value") String styleValue) {
    styleMap.put(styleName, styleValue);
  }

  @ParameterSuggestion(action = "style", parameter = "name")
  public List<Suggestion> suggestStyles() {
    return styleMap.styles().stream()
      .map(style -> PromptSuggestion.suggest(style)
        .describedAs(styleMap.styleOf(style)))
      .collect(Collectors.toList());
  }

  @Action("styles")
  public void printStyles(Writer writer) {
    styleMap.styles().forEach(name -> {
        String mappedStyle = styleMap.styleOf(name);
        writer.write(name).write(": ")
          .withStyle(mappedStyle).writeln(mappedStyle);
      }
    );
  }

  @Action("color")
  public void manageColor(@InputParameter("name") String colorName,
                          @InputParameter("value") String colorValue) {
    colorMap.put(colorName, colorValue);
  }

  @ParameterSuggestion(action = "color", parameter = "name")
  @ParameterSuggestion(action = "color", parameter = "value")
  public List<Suggestion> suggestColors() {
    return colorMap.colors().stream()
      .map(color -> PromptSuggestion.suggest(color)
        .describedAs(colorMap.valueOf(color)))
      .collect(Collectors.toList());
  }

  @Action("colors")
  public void printColors(Writer writer) {
    colorMap.colors().forEach(color ->
      writer.write(color).write(": ")
        .withStyle(color).writeln(colorMap.valueOf(color)));
  }

  @Action("icon")
  public void manageIcon(@InputParameter("name") String iconName,
                         @InputParameter("value") String iconValue) {
    iconMap.put(iconName, iconValue);
  }

  @ParameterSuggestion(action = "icon", parameter = "name")
  public List<Suggestion> suggestIcons() {
    return iconMap.icons().stream()
      .map(icon -> PromptSuggestion.suggest(icon)
        .describedAs(iconMap.symbolOf(icon)))
      .collect(Collectors.toList());
  }

  @Action("icons")
  public void printIcons(Writer writer) {
    iconMap.icons().forEach(icon ->
      writer.style().set()
        .write(icon)
        .write(": ")
        .writeIcon(icon)
        .newLine());
  }

}

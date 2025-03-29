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

import com.backpackcloud.cli.Preferences;
import com.backpackcloud.cli.ui.StyleBuilder;
import com.backpackcloud.cli.ui.Theme;
import com.backpackcloud.preferences.UserPreferences;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.Parser;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class PromptHighlighter implements Highlighter {

  private final UserPreferences userPreferences;
  private final Set<String> commands;
  private final Theme theme;

  public PromptHighlighter(UserPreferences userPreferences,
                           Set<String> commands,
                           Theme theme) {
    this.theme = theme;
    this.commands = new HashSet<>(commands);
    this.userPreferences = userPreferences;
  }

  public void addCommand(String command) {
    commands.add(command);
  }

  @Override
  public AttributedString highlight(LineReader reader, String buffer) {
    if (userPreferences.isDisabled(Preferences.HIGHLIGHTER)) {
      return new AttributedString(buffer);
    }
    Parser parser = reader.getParser();
    String command = parser.getCommand(buffer);
    String remaining = buffer.substring(buffer.indexOf(command) + command.length());

    String commandColor;
    String commandArgsColor;

    boolean commandError = false;

    if (command.isBlank() || commands.contains(command)) {
      commandColor = "highlighter_valid_command//bi";
      commandArgsColor = "highlighter_valid_command_args";
    } else {
      commandColor = "highlighter_invalid_command";
      commandArgsColor = "highlighter_invalid_command_args";
      commandError = true;
    }

    AttributedStringBuilder builder = new AttributedStringBuilder()
      .style(StyleBuilder.newSimpleBuilder(theme.colorMap()).parse(commandColor).set())
      .append(command)
      .style(StyleBuilder.newSimpleBuilder(theme.colorMap()).parse(commandArgsColor).set())
      .append(remaining);

    if (commandError) {
      builder.append("  ")
        .style(AttributedStyle::blink)
        .append(theme.iconMap().symbolOf("warning"));
    }

    return builder.toAttributedString();
  }

  @Override
  public void setErrorPattern(Pattern errorPattern) {

  }

  @Override
  public void setErrorIndex(int errorIndex) {

  }

}

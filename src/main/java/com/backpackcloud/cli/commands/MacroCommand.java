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

import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.Macro;
import com.backpackcloud.text.InputValue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacroCommand implements Command {

  private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{(?<parameter>[^}]+)}");

  private final Macro macro;

  public MacroCommand(Macro macro) {
    this.macro = macro;
  }

  @Override
  public String name() {
    return macro.name();
  }

  @Override
  public String type() {
    return "Macros";
  }

  @Override
  public String description() {
    return macro.description();
  }

  @Override
  public void execute(CommandContext context) {
    List<InputValue> args = context.input().words();
    macro.commands().stream()
      .map(input -> {
        Matcher matcher = PARAMETER_PATTERN.matcher(input);

        return matcher.replaceAll(result -> {
          String[] split = result.group("parameter").split(":");
          int index = Integer.parseInt(split[0]);
          String defaultValue = split.length > 1 ? split[1] : "";
          if (args.size() > index) {
            return args.get(index).get();
          } else {
            return defaultValue;
          }
        });
      })
      .forEach(context.cli()::execute);
  }

}

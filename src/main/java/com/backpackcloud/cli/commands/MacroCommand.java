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

import com.backpackcloud.text.Interpolator;
import com.backpackcloud.cli.CLI;
import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.CommandInput;

import java.util.List;

public class MacroCommand implements Command {

  private final String name;
  private final CLI cli;
  private final List<String> commands;

  public MacroCommand(String name, CLI cli, List<String> commands) {
    this.name = name;
    this.cli = cli;
    this.commands = commands;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String type() {
    return "Macro";
  }

  @Override
  public String description() {
    return "User defined macro";
  }

  @Override
  public void execute(CommandContext context) {
    List<CommandInput> args = context.input().asList();
    Interpolator interpolator = new Interpolator(s -> {
      String[] split = s.split(":");
      int index = Integer.parseInt(split[0]);
      String defaultValue = split.length > 1 ? split[1] : "";
      if (args.size() > index) {
        return args.get(index).asString();
      } else {
        return defaultValue;
      }
    });
    commands.stream()
      .map(interpolator::eval)
      .forEach(command -> command.ifPresent(cli::execute));
  }

}

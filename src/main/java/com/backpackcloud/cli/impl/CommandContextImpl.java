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

package com.backpackcloud.cli.impl;

import com.backpackcloud.cli.CLI;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.CommandInput;
import com.backpackcloud.cli.Writer;

import java.util.List;

public class CommandContextImpl implements CommandContext {

  private final CLI cli;
  private final List<String> args;
  private final Writer writer;
  private final boolean interactive;

  public CommandContextImpl(CLI cli, List<String> args, Writer writer, boolean interactive) {
    this.cli = cli;
    this.args = args;
    this.writer = writer;
    this.interactive = interactive;
  }

  public boolean isInteractive() {
    return interactive;
  }

  @Override
  public CLI cli() {
    return cli;
  }

  @Override
  public CommandInput input() {
    return new CommandInputImpl(String.join(" ", args), args);
  }

  @Override
  public Writer writer() {
    return writer;
  }

}

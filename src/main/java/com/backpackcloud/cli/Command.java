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

package com.backpackcloud.cli;

import com.backpackcloud.cli.ui.Suggestion;

import java.util.Collections;
import java.util.List;

/**
 * Defines a command that can be issued by the user via the interactive interface.
 *
 * @author Marcelo "Ataxexe" Guimarães
 */
public interface Command {

  /**
   * @return the type of this command.
   */
  String type();

  /**
   * Returns the name of the command, which the user needs to type to execute it.
   *
   * @return the name of the command.
   */
  String name();

  /**
   * @return a brief description of this command.
   */
  String description();

  /**
   * Executes this command based on the given context.
   *
   * @param context the context in which this command was issued.
   */
  void execute(CommandContext context);

  /**
   * Suggest a list of candidates to the user to help them decide which
   * arguments to fill.
   *
   * @param input the current input argument.
   * @return a list of possible candidates to present to the user.
   */
  default List<Suggestion> suggest(CommandInput input) {
    return Collections.emptyList();
  }

}

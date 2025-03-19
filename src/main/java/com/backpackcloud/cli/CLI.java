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

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.ui.Prompt;
import com.backpackcloud.cli.ui.PromptWriter;
import com.backpackcloud.cli.ui.Theme;
import com.backpackcloud.cli.ui.components.CommandCompleter;
import com.backpackcloud.cli.ui.components.PromptHighlighter;
import com.backpackcloud.preferences.UserPreferences;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CLI {

  private final Terminal terminal;
  private final UserPreferences preferences;
  private final Theme theme;

  private final LineReader lineReader;
  private final Map<String, Command> commands;

  private final PromptHighlighter highlighter;

  private final Collection<PromptWriter> leftPrompt;
  private final Collection<PromptWriter> rightPrompt;
  private final CommandBus commandBus;

  private final Writer console;

  private boolean stop;

  public CLI(Terminal terminal,
             UserPreferences preferences,
             Theme theme,
             CommandBus commandBus) {
    this.terminal = terminal;
    this.preferences = preferences;
    this.theme = theme;
    this.leftPrompt = new ArrayList<>();
    this.rightPrompt = new ArrayList<>();
    this.commandBus = commandBus;

    this.commands = new HashMap<>();

    this.highlighter = new PromptHighlighter(preferences, new HashSet<>(), theme);

    this.lineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .highlighter(highlighter)
      .history(new DefaultHistory())
      .completer(new CommandCompleter(this.commands, preferences))
      .build();

    this.console = new Writer(
      theme,
      AttributedStyle.DEFAULT,
      AttributedString::new,
      text -> terminal.writer().print(text.toAnsi()),
      terminal
    );

    this.lineReader.option(LineReader.Option.DISABLE_EVENT_EXPANSION, true);

    preferences.watch(Preferences.AUTO_SUGGEST, enabled -> {
      if (enabled) {
        this.lineReader.setAutosuggestion(LineReader.SuggestionType.COMPLETER);
      } else {
        this.lineReader.setAutosuggestion(LineReader.SuggestionType.NONE);
      }
    });
  }

  public void addLeftPrompt(PromptWriter writer) {
    this.leftPrompt.add(writer);
  }

  public void addRightPrompt(PromptWriter writer) {
    this.rightPrompt.add(writer);
  }

  public void register(Command... commands) {
    for (Command command : commands) {
      String name = command.name();
      this.commands.put(name, command);
      this.highlighter.addCommand(name);
    }
  }

  public void stop() {
    stop = true;
  }

  private void flush() {
    terminal.flush();
  }

  public void start() {
    String query;
    while (!stop) {
      try {
        commandBus.notifyReady();

        String left = buildLeftPrompt();
        String right = buildRightPrompt();

        query = lineReader.readLine(left, right, (Character) null, null).trim();

        execute(query);
      } catch (UnbelievableException e) {
        commandBus.notifyError(e);
        if (e.getMessage() != null) {
          console.style()
            .parse("command_error")
            .bold().italic()
            .set().write(e.getMessage()).newLine();
        } else {
          console.style()
            .parse("command_error")
            .bold().italic()
            .set().write("An error occurred").newLine();
        }
      } catch (EndOfFileException e) {
        // if ctrl+d is pressed, exit cli
        return;
      } catch (UserInterruptException e) {
        // exit cli if there's no input
        // this would make it easy to just cancel the current prompt and start again
        // if ctrl+c is pressed without any input, cli will just end
        if (e.getPartialLine().isBlank()) {
          return;
        }
      }
    }
  }

  public void execute(String... commands) {
    execute(console, commands);
  }

  public void execute(Writer writer, String... commands) {
    commandBus.notifyStart();
    try {
      for (String command : commands) {
        if (!command.isEmpty()) {
          parseAndExecute(writer, lineReader.getParser().parse(command, 0));
        }
      }
    } catch (Exception e) {
      commandBus.notifyError(e);
    } finally {
      commandBus.notifyDone();
    }
  }

  private String buildLeftPrompt() {
    StringBuilder builder = new StringBuilder();
    Writer writer = stringBuilderWriter(builder);
    Prompt prompt = Prompt.create(theme, writer, terminal,
      preferences.supplier(Preferences.LEFT_PROMPT_TAIL).get(),
      preferences.supplier(Preferences.LEFT_PROMPT_SEPARATOR).get(),
      preferences.supplier(Preferences.LEFT_PROMPT_HEAD).get()
    );

    leftPrompt.forEach(promptWriter -> promptWriter.addTo(prompt, PromptWriter.PromptSide.LEFT));

    return builder.toString();
  }

  private String buildRightPrompt() {
    StringBuilder builder = new StringBuilder();
    Writer writer = stringBuilderWriter(builder);
    Prompt prompt = Prompt.create(theme, writer, terminal,
      preferences.supplier(Preferences.RIGHT_PROMPT_TAIL).get(),
      preferences.supplier(Preferences.RIGHT_PROMPT_SEPARATOR).get(),
      preferences.supplier(Preferences.RIGHT_PROMPT_HEAD).get()
    );

    rightPrompt.forEach(promptWriter -> promptWriter.addTo(prompt, PromptWriter.PromptSide.RIGHT));

    prompt.closeSegments();

    return builder.toString();
  }

  private Writer stringBuilderWriter(StringBuilder stringBuilder) {
    return new Writer(theme, AttributedStyle.DEFAULT,
      AttributedString::new,
      text -> stringBuilder.append(text.toAnsi()),
      terminal);
  }

  private void parseAndExecute(Writer writer, ParsedLine parsedLine) {
    String commandName = parsedLine.words().getFirst();
    Command command = commands.get(commandName);
    CommandContext currentContext;

    if (command == null) {
      throw new UnbelievableException("Unknown command " + commandName);
    }

    currentContext = new CommandContext(this, parsedLine, writer);
    command.execute(currentContext);
  }

}

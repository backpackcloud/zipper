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

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.CLI;
import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandNotifier;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.Writers;
import com.backpackcloud.cli.commands.MacroCommand;
import com.backpackcloud.cli.preferences.UserPreference;
import com.backpackcloud.cli.preferences.UserPreferences;
import com.backpackcloud.cli.ui.Prompt;
import com.backpackcloud.cli.ui.PromptWriter;
import com.backpackcloud.cli.ui.Theme;
import com.backpackcloud.cli.ui.impl.CommandCompleter;
import com.backpackcloud.cli.ui.impl.PromptHighlighter;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseCLI implements CLI {

  private final UserPreferences preferences;
  private final Theme theme;

  private final LineReader lineReader;
  private final Map<String, Command> commands;

  private final PromptHighlighter highlighter;

  private final Collection<PromptWriter> leftPrompt;
  private final Collection<PromptWriter> rightPrompt;
  private final CommandNotifier notifier;

  private final Writer console;

  private boolean stop;

  public BaseCLI(Terminal terminal,
                 UserPreferences preferences,
                 Theme theme,
                 Collection<Command> commands,
                 Writer consoleWriter,
                 Collection<PromptWriter> leftPrompt,
                 Collection<PromptWriter> rightPrompt,
                 CommandNotifier notifier) {
    this.preferences = preferences;
    this.theme = theme;
    this.leftPrompt = leftPrompt;
    this.rightPrompt = rightPrompt;
    this.notifier = notifier;
    this.console = consoleWriter;

    this.commands = new HashMap<>();

    commands.forEach(command -> {
      this.commands.put(command.name(), command);
      command.aliases().forEach(alias -> this.commands.put(alias, command));
    });

    this.highlighter = new PromptHighlighter(preferences, this.commands.keySet(), theme);

    this.lineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .highlighter(highlighter)
      .history(new DefaultHistory())
      .completer(new CommandCompleter(this.commands, preferences))
      .build();

    this.lineReader.option(LineReader.Option.DISABLE_EVENT_EXPANSION, true);

    preferences.watchFlag(UserPreference.AUTO_SUGGEST, enabled -> {
      if (enabled) {
        this.lineReader.setAutosuggestion(LineReader.SuggestionType.COMPLETER);
      } else {
        this.lineReader.setAutosuggestion(LineReader.SuggestionType.NONE);
      }
    });
  }

  @Override
  public void registerMacro(String name, List<String> commands) {
    this.commands.put(name, new MacroCommand(name, this, commands));
    this.highlighter.addCommand(name);
  }

  @Override
  public void stop() {
    stop = true;
  }

  @Override
  public void start() {
    String query;
    while (!stop) {
      try {
        notifier.notifyReady();

        String left = buildLeftPrompt();
        String right = buildRightPrompt();

        query = lineReader.readLine(left, right, (Character) null, null).trim();

        execute(query);
      } catch (UnbelievableException e) {
        notifier.notifyError(e);
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

  @Override
  public void execute(String... commands) {
    execute(console, commands);
  }

  @Override
  public void execute(Writer writer, String... commands) {
    notifier.notifyStart();
    try {
      for (String command : commands) {
        if (!command.isEmpty()) {
          parseAndExecute(writer, lineReader.getParser().parse(command, 0));
        }
      }
    } catch (Exception e) {
      notifier.notifyError(e);
    } finally {
      notifier.notifyDone();
    }
  }

  private String buildLeftPrompt() {
    StringBuilder builder = new StringBuilder();
    Writer writer = Writers.forStringBuilder(builder, theme);
    Prompt prompt = Prompt.create(theme, writer,
      preferences.text(UserPreference.LEFT_PROMPT_TAIL).get(),
      preferences.text(UserPreference.LEFT_PROMPT_SEPARATOR).get(),
      preferences.text(UserPreference.LEFT_PROMPT_HEAD).get()
    );

    leftPrompt.forEach(promptWriter -> promptWriter.addTo(prompt, PromptWriter.PromptSide.LEFT));

    return builder.toString();
  }

  private String buildRightPrompt() {
    StringBuilder builder = new StringBuilder();
    Writer writer = Writers.forStringBuilder(builder, theme);
    Prompt prompt = Prompt.create(theme, writer,
      preferences.text(UserPreference.RIGHT_PROMPT_TAIL).get(),
      preferences.text(UserPreference.RIGHT_PROMPT_SEPARATOR).get(),
      preferences.text(UserPreference.RIGHT_PROMPT_HEAD).get()
    );

    rightPrompt.forEach(promptWriter -> promptWriter.addTo(prompt, PromptWriter.PromptSide.RIGHT));

    prompt.closeSegments();

    return builder.toString();
  }

  private void parseAndExecute(Writer writer, ParsedLine parsedLine) {
    List<String> args = new ArrayList<>(parsedLine.words());
    String commandName = args.get(0);
    Command command = commands.get(commandName);
    BufferedWriter fileWriter = null;
    boolean interactive = true;
    CommandContextImpl currentContext;

    if (command == null) {
      throw new UnbelievableException("Unknown command " + commandName);
    }

    if (command.allowOutputRedirect() && (args.contains(">") && args.indexOf(">") == args.size() - 2)) {
      int pos = args.indexOf(">");
      String fileName = args.get(pos + 1);

      try {
        Path parent = Path.of(fileName).getParent();
        if (parent != null) {
          Files.createDirectories(parent);
        }
        fileWriter = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8));
      } catch (IOException e) {
        throw new UnbelievableException(e);
      }
      writer = new OutputRedirectWriter(theme, fileWriter);
      args = args.subList(0, pos);
      interactive = false;
    }

    List<String> argsList = args.subList(1, args.size());

    try {
      // only allow interaction if cli is taking user inputs
      currentContext = new CommandContextImpl(this, argsList, writer, interactive);
      command.execute(currentContext);
    } finally {
      try {
        if (fileWriter != null) {
          fileWriter.close();
        }
      } catch (IOException e) {
        console.style()
          .parse("command_error")
          .bold().italic()
          .set().write(e.getMessage()).newLine();
        notifier.notifyError(e);
      }
    }
  }

}

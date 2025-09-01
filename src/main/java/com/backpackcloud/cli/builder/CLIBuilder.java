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

package com.backpackcloud.cli.builder;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.*;
import com.backpackcloud.cli.commands.AnnotatedCommand;
import com.backpackcloud.cli.commands.ClearCommand;
import com.backpackcloud.cli.commands.ExitCommand;
import com.backpackcloud.cli.commands.MacroCommand;
import com.backpackcloud.cli.commands.PreferencesCommand;
import com.backpackcloud.cli.commands.ShowErrorRegistryCommand;
import com.backpackcloud.cli.commands.ThemeCommand;
import com.backpackcloud.cli.ui.PromptWriter;
import com.backpackcloud.cli.ui.Theme;
import com.backpackcloud.cli.ui.prompt.CommandStatusPromptWriter;
import com.backpackcloud.cli.ui.prompt.ErrorCountPromptWriter;
import com.backpackcloud.cli.ui.prompt.PromptCharWriter;
import com.backpackcloud.cli.ui.prompt.TimerPromptWriter;
import com.backpackcloud.io.Deserializer;
import com.backpackcloud.io.SerialBitter;
import com.backpackcloud.io.Serializer;
import com.backpackcloud.preferences.Preference;
import com.backpackcloud.preferences.PreferenceSpec;
import com.backpackcloud.preferences.UserPreferences;
import com.backpackcloud.reflection.Context;
import com.backpackcloud.reflection.Mirror;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.backpackcloud.reflection.predicates.ParameterPredicates.ofType;

public class CLIBuilder {

  private final SerialBitter serialBitter;
  private final Terminal terminal;
  private final UserPreferences userPreferences;
  private final Theme theme;
  private final ErrorRegistry errorRegistry;
  private final CommandBus commandBus;
  private final List<Registry> registries;
  private final List<Command> commands;
  private final List<PromptWriter> leftPromptWriters;
  private final List<PromptWriter> rightPromptWriters;
  private final Context context;
  private final EventBus eventBus;

  public CLIBuilder(SerialBitter serialBitter) {
    this.serialBitter = serialBitter;
    this.terminal = createTerminal();
    this.userPreferences = createUserPreferences();
    this.theme = Theme.create(serialBitter);
    this.errorRegistry = new ErrorRegistry();
    this.commandBus = new CommandBus(errorRegistry);
    this.commands = new ArrayList<>();
    this.leftPromptWriters = new ArrayList<>();
    this.rightPromptWriters = new ArrayList<>();
    this.context = new Context();
    this.eventBus = new EventBus();

    this.registries = new ArrayList<>();
    this.registries.add(errorRegistry);

    initializeContext();
  }

  private void initializeContext() {
    addComponent(serialBitter, SerialBitter.class, Serializer.class, Deserializer.class);
    addComponent(commandBus, CommandBus.class, CommandObserver.class, CommandNotifier.class);
    addComponent(userPreferences, UserPreferences.class);
    addComponent(theme, Theme.class);
    addComponent(errorRegistry, ErrorRegistry.class);
    addComponent(eventBus, EventBus.class);
    addComponent(
      Preference.class,
      parameter -> userPreferences.find(parameter.getName())
        .orElseThrow(UnbelievableException.because("Preference " + parameter.getName() + " not registered."))
    );
  }

  public CLIBuilder addComponent(Class<?> componentType) {
    return addComponent(createComponent(componentType), componentType);
  }

  public CLIBuilder addComponent(Object component, Class... componentTypes) {
    for (Class type : componentTypes) {
      this.context.when(ofType(type), component);
      this.serialBitter.addDependency(type, component);
    }

    if (component instanceof Registry registry) {
      this.registries.add(registry);
    }

    return this;
  }

  public <T> CLIBuilder addComponent(Class<T> componentType, Supplier<T> componentSupplier) {
    this.context.when(ofType(componentType), componentSupplier);
    return this;
  }

  public <T> CLIBuilder addComponent(Class<T> componentType, Function<Parameter, T> componentResolver) {
    this.context.when(ofType(componentType), componentResolver);
    return this;
  }

  public CLIBuilder addCommands(Class... commandClasses) {
    for (Class<?> commandClass : commandClasses) {
      addCommand(commandClass);
    }
    return this;
  }

  public CLIBuilder addCommand(Class<?> commandClass) {
    Object command = createComponent(commandClass);
    if (command instanceof Command c) {
      return addCommand(c);
    }
    return addCommand(new AnnotatedCommand(command, eventBus, userPreferences, terminal));
  }

  public CLIBuilder addCommand(Command command) {
    this.commands.add(command);
    return this;
  }

  public CLIBuilder addCommand(Object command) {
    this.commands.add(new AnnotatedCommand(command, eventBus, userPreferences, terminal));
    return this;
  }

  public CLIBuilder addMacro(Macro macro) {
    return addCommand(new MacroCommand(macro));
  }

  public CLIBuilder addMacros(List<Macro> macros) {
    for (Macro macro : macros) {
      addMacro(macro);
    }
    return this;
  }

  public CLIBuilder addLeftPrompt(Class<? extends PromptWriter>... promptWriterTypes) {
    for (Class<? extends PromptWriter> promptWriterType : promptWriterTypes) {
      this.leftPromptWriters.add(createComponent(promptWriterType));
    }
    return this;
  }

  public CLIBuilder addLeftPrompt(PromptWriter... promptWriters) {
    for (PromptWriter promptWriter : promptWriters) {
      this.leftPromptWriters.add(promptWriter);
    }
    return this;
  }

  public CLIBuilder addRightPrompt(Class<? extends PromptWriter>... promptWriterTypes) {
    for (Class<? extends PromptWriter> promptWriterType : promptWriterTypes) {
      this.rightPromptWriters.add(createComponent(promptWriterType));
    }
    return this;
  }

  public CLIBuilder addRightPrompt(PromptWriter... promptWriters) {
    for (PromptWriter promptWriter : promptWriters) {
      this.rightPromptWriters.add(promptWriter);
    }
    return this;
  }

  public CLIBuilder registerPreferences(Class container) {
    this.userPreferences.register(container);
    return this;
  }

  public CLIBuilder registerPreferences(PreferenceSpec... specs) {
    this.userPreferences.register(specs);
    return this;
  }

  public CLIBuilder addDefaultRightPrompts() {
    addRightPrompt(CommandStatusPromptWriter.class);
    addRightPrompt(ErrorCountPromptWriter.class);
    addRightPrompt(TimerPromptWriter.class);
    return this;
  }

  private <E> E createComponent(Class<E> componentType) {
    List<Constructor> constructors = Mirror.reflect(componentType).constructors();
    if (constructors.size() != 1) {
      throw new UnbelievableException("CLI component should have one public constructor");
    }
    Constructor<?> constructor = constructors.getFirst();
    Object[] args = this.context.resolve(constructor);
    try {
      E component = (E) constructor.newInstance(args);
      eventBus.scan(component);
      return component;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new UnbelievableException(e);
    } catch (InvocationTargetException e) {
      throw new UnbelievableException(e.getTargetException());
    }
  }

  private UserPreferences createUserPreferences() {
    UserPreferences preferences = new UserPreferences();
    preferences.register(Preferences.class);

    return preferences;
  }

  private Terminal createTerminal() {
    try {
      return TerminalBuilder.builder()
        .system(true)
        .build();
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  private void initializeCommands() {
    addCommand(new ClearCommand(this.registries));
    addCommands(ExitCommand.class, PreferencesCommand.class, ShowErrorRegistryCommand.class, ThemeCommand.class);
  }

  private void initializeLeftPrompt() {
    addLeftPrompt(PromptCharWriter.class);
  }

  private void initializeRightPrompt() {
    addDefaultRightPrompts();
  }

  public CLI build() {
    CLI cli = new CLI(
      terminal,
      userPreferences,
      theme,
      commandBus
    );
    initializeCommands();
    commands.forEach(cli::register);
    if (leftPromptWriters.isEmpty()) {
      initializeLeftPrompt();
    }
    if (rightPromptWriters.isEmpty()) {
      initializeRightPrompt();
    }
    leftPromptWriters.forEach(cli::addLeftPrompt);
    rightPromptWriters.forEach(cli::addRightPrompt);
    return cli;
  }

}

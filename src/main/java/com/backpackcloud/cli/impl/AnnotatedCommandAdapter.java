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
import com.backpackcloud.cli.*;
import com.backpackcloud.cli.preferences.Preference;
import com.backpackcloud.cli.preferences.UserPreferences;
import com.backpackcloud.cli.ui.Paginator;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.impl.PaginatorImpl;
import com.backpackcloud.cli.ui.impl.PromptSuggestion;
import com.backpackcloud.trugger.factory.Context;
import com.backpackcloud.trugger.factory.ContextFactory;
import com.backpackcloud.trugger.reflection.ReflectedMethod;
import com.backpackcloud.trugger.reflection.Reflection;
import com.backpackcloud.trugger.reflection.ReflectionPredicates;
import org.jline.terminal.Terminal;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotatedCommandAdapter implements Command {

  private final AnnotatedCommand command;

  private final Map<String, ReflectedMethod> actions;

  private final Map<String, ReflectedMethod> suggestions;

  private final UserPreferences preferences;
  private final Terminal terminal;

  private final CommandDefinition definition;

  public AnnotatedCommandAdapter(AnnotatedCommand command, UserPreferences preferences, Terminal terminal) {
    this.command = command;
    this.preferences = preferences;
    this.terminal = terminal;
    this.actions = new HashMap<>();
    this.suggestions = new HashMap<>();
    this.definition = command.getClass().getAnnotation(CommandDefinition.class);
    initialize();
  }

  private void initialize() {
    Reflection.reflect()
      .methods()
      .annotatedWith(Action.class)
      .from(command)
      .forEach(method -> {
        Action action = method.getAnnotation(Action.class);
        String actionName = action.value();

        actions.put(actionName, method);
      });

    if (actions.isEmpty()) {
      throw new UnbelievableException("No action mapped for " + command.getClass());
    }

    Reflection.reflect()
      .methods()
      .annotatedWith(Suggestions.class)
      .from(command)
      .forEach(method -> Stream.of(method.getAnnotation(Suggestions.class).value())
        .forEach(action -> suggestions.put(action, method)));
  }

  @Override
  public void execute(CommandContext context) {
    if (actions.size() == 1) {
      invokeAction(context, actions.get(""), context.input().asList()); // ugly as my ass, but I'll improve it later (you know I won't, right?)
    } else {
      List<CommandInput> input = context.input().asList();
      if (input.isEmpty()) {
        throw new UnbelievableException("No action given");
      }
      String actionName = input.iterator().next().asString();
      if (actions.containsKey(actionName)) {
        invokeAction(context, actions.get(actionName), input.size() > 1 ? input.subList(1, input.size()) : Collections.emptyList());
      } else {
        throw new UnbelievableException("Action " + actionName + " not recognized");
      }
    }
  }

  private void invokeAction(CommandContext commandContext, ReflectedMethod actionMethod, List<CommandInput> commandInputs) {
    Object[] args = resolveArgs(commandContext, commandInputs, actionMethod.unwrap());

    if (actionMethod.isAnnotationPresent(Paginate.class)) {
      Paginate annotation = actionMethod.getAnnotation(Paginate.class);
      PaginatorImpl paginator = new PaginatorImpl(preferences, terminal, commandContext);

      List<?> resultList = actionMethod.invoke(args);

      paginator.from(resultList)
        .print(this::printReturn)
        .pageSize(annotation.pageSize())
        .paginate();
    } else {
      printReturn(commandContext.writer(), actionMethod.invoke(args));
    }

  }

  private void printReturn(Writer writer, Object value) {
    if (value instanceof String output) {
      writer.writeln(output);
    } else if (value instanceof Displayable displayable) {
      writer.writeln(displayable);
    } else if (value instanceof Iterable<?> iterable) {
      for (Object o : iterable) {
        printReturn(writer, o);
      }
    } else if (value != null) {
      writer.writeln(String.valueOf(value));
    }
  }

  @Override
  public List<Suggestion> suggest(CommandInput commandInput) {
    if (actions.size() == 1) {
      return invokeSuggestion(suggestions.get(""), commandInput.asList());
    } else {
      List<CommandInput> input = commandInput.asList();
      if (input.size() <= 1) {
        return actions.keySet().stream()
          .map(PromptSuggestion::suggest)
          .collect(Collectors.toList());
      }
      String actionName = input.iterator().next().asString();
      if (suggestions.containsKey(actionName)) {
        return invokeSuggestion(suggestions.get(actionName), input.size() > 1 ? input.subList(1, input.size()) : Collections.emptyList());
      } else {
        return Collections.emptyList();
      }
    }
  }

  private List<Suggestion> invokeSuggestion(ReflectedMethod suggestionMethod, List<CommandInput> commandInputs) {
    Object[] args = resolveArgs(null, commandInputs, suggestionMethod.unwrap());
    return suggestionMethod.invoke(args);
  }

  private Object[] resolveArgs(CommandContext commandContext, List<CommandInput> commandInputs, Executable executable) {
    ContextFactory contextFactory = new ContextFactory();
    Context context = contextFactory.context();

    Iterator<CommandInput> inputIterator = commandInputs.iterator();
    Supplier<String> inputSupplier = () -> {
      if (inputIterator.hasNext()) {
        return inputIterator.next().asString();
      }
      return null;
    };

    if (commandContext != null) {
      context
        .whenOfType(CommandContext.class)
        .use(commandContext)

        .whenOfType(Writer.class)
        .use(commandContext::writer);
    }

    context
      .whenOfType(Preference.class)
      .use(parameter -> preferences.find(resolvePreferenceId(parameter.getName()))
        .or(() -> preferences.find(inputSupplier.get()))
        .orElseThrow(() -> new UnbelievableException("Preference not found for parameter: " + parameter.getName())))

      .whenAnnotatedWith(PreferenceValue.class)
      .use(parameter -> {
        String preferenceId = parameter.getAnnotation(PreferenceValue.class).value();
        if (preferenceId.isBlank()) {
          preferenceId = resolvePreferenceId(parameter.getName());
        }
        return preferences.find(preferenceId)
          .orElseThrow(() -> new UnbelievableException("Preference not found for parameter: " + parameter.getName()))
          .value();
      })

      .whenOfType(CommandInput.class)
      .use(() -> inputIterator.hasNext() ? inputIterator.next() : null)

      .whenAnnotatedWith(RawInput.class)
      .use(() -> {
        List<String> args = new ArrayList<>();
        inputIterator.forEachRemaining(i -> args.add(i.asString()));
        return String.join(" ", args);
      })

      .whenOfType(String[].class)
      .use(() -> {
        List<String> args = new ArrayList<>();
        inputIterator.forEachRemaining(i -> args.add(i.asString()));
        return args.toArray(new String[0]);
      })

      .whenOfType(Paginator.class)
      .use(() -> new PaginatorImpl(preferences, terminal, commandContext))

      .whenOfType(String.class)
      .use(inputSupplier)

      .whenOfType(Integer.class)
      .use(() -> inputIterator.hasNext() ? inputIterator.next().asInt().orElse(null) : null)

      .whenOfType(Enum.class)
      .use(parameter -> {
        String name = inputSupplier.get();
        if (name != null) {
          return Enum.valueOf((Class<? extends Enum>) parameter.getType(), resolveEnumName(name));
        }
        return null;
      })

      .orElse()
      .use(parameter ->
        Reflection.reflect()
          .method("valueOf")
          .filter(ReflectionPredicates.declared(Modifier.STATIC))
          .withParameters(String.class)
          .from(parameter.getType())
          .map(method -> method.invoke(inputIterator.next().asString()))
          .orElse(null)
      );

    return contextFactory.resolveArgs(executable);
  }

  private String resolvePreferenceId(String name) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (Character.isLowerCase(c)) {
        result.append(c);
      } else {
        result.append("-").append(Character.toLowerCase(c));
      }
    }
    return result.toString();
  }

  private String resolveEnumName(String name) {
    return name.toUpperCase().replaceAll("-", "_");
  }

  @Override
  public String name() {
    return definition.name();
  }

  @Override
  public String type() {
    return definition.type();
  }

  @Override
  public String description() {
    return definition.description();
  }

  @Override
  public boolean allowOutputRedirect() {
    return definition.allowOutputRedirect();
  }

  @Override
  public List<String> aliases() {
    return List.of(definition.aliases());
  }

}

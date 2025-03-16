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

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.*;
import com.backpackcloud.cli.ui.Paginator;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.components.PromptSuggestion;
import com.backpackcloud.preferences.Preference;
import com.backpackcloud.preferences.UserPreferences;
import com.backpackcloud.reflection.Context;
import com.backpackcloud.reflection.Mirror;
import org.jline.terminal.Terminal;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.backpackcloud.reflection.predicates.ParameterPredicates.annotatedWith;
import static com.backpackcloud.reflection.predicates.ParameterPredicates.ofType;

public class AnnotatedCommandAdapter implements Command {

  private final AnnotatedCommand command;

  private final Map<String, Method> actions;

  private final Map<String, Method> suggestions;

  private final UserPreferences preferences;
  private final Terminal terminal;

  private final CommandDefinition definition;

  public AnnotatedCommandAdapter(AnnotatedCommand command,
                                 UserPreferences preferences,
                                 Terminal terminal) {
    this.command = command;
    this.preferences = preferences;
    this.terminal = terminal;
    this.actions = new HashMap<>();
    this.suggestions = new HashMap<>();
    this.definition = command.getClass().getAnnotation(CommandDefinition.class);
    initialize();
  }

  private void initialize() {
    List<Method> methods = Mirror.reflect(command).methods();

    methods.stream()
      .filter(method -> method.isAnnotationPresent(Action.class))
      .forEach(method -> {
        Action action = method.getAnnotation(Action.class);
        String actionName = action.value();

        if (actionName.isBlank()) {
          actionName = method.getName();
        }

        actions.put(actionName, method);
      });

    if (actions.isEmpty()) {
      throw new UnbelievableException("No action mapped for " + command.getClass());
    }

    methods.stream()
      .filter(method -> method.isAnnotationPresent(Suggestions.class))
      .forEach(method -> {
        String[] actions = method.getAnnotation(Suggestions.class).value();
        if (actions.length == 0) {
          actions = new String[]{method.getName()};
        }
        Stream.of(actions)
          .forEach(action -> suggestions.put(action, method));
      });
  }

  @Override
  public void execute(CommandContext context) {
    List<CommandInput> input = context.input().asList();
    String event = definition.event().isEmpty() ? definition.name() : definition.event();
    if (actions.size() == 1) {
      invokeAction(context, actions.values().iterator().next(), input);
    } else {
      if (input.isEmpty()) {
        throw new UnbelievableException("No action given");
      }
      String actionName = input.getFirst().asString();
      if (actions.containsKey(actionName)) {
        invokeAction(context, actions.get(actionName), input.size() > 1 ? input.subList(1, input.size()) : Collections.emptyList());
      } else {
        throw new UnbelievableException("Action " + actionName + " not recognized");
      }
    }
  }

  private void invokeAction(CommandContext commandContext, Method actionMethod, List<CommandInput> commandInputs) {
    Object[] args = resolveArgs(commandContext, commandInputs, actionMethod);

    Object returnValue;

    try {
      returnValue = actionMethod.invoke(command, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new UnbelievableException(e);
    }

    if (actionMethod.isAnnotationPresent(Paginate.class)) {
      Paginate annotation = actionMethod.getAnnotation(Paginate.class);
      Paginator paginator = new Paginator(preferences, terminal, commandContext);

      if (returnValue instanceof List<?> returnList) {
        paginator.from(returnList)
          .print(this::printReturn)
          .pageSize(annotation.pageSize())
          .paginate();
      } else if (returnValue instanceof Stream<?> returnStream) {
        paginator.from(returnStream)
          .print(this::printReturn)
          .pageSize(annotation.pageSize())
          .paginate();
      } else {
        throw new UnbelievableException("Unable to paginate return object, only streams and lists are supported.");
      }
    } else {
      printReturn(commandContext.writer(), returnValue);
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
    } else if (value instanceof Stream<?> stream) {
      stream.forEach(obj -> printReturn(writer, obj));
    } else if (value != null) {
      writer.writeln(String.valueOf(value));
    }
  }

  @Override
  public List<Suggestion> suggest(CommandInput commandInput) {
    if (actions.size() == 1) {
      return invokeSuggestion(suggestions.values().iterator().next(), commandInput.asList());
    } else {
      List<CommandInput> input = commandInput.asList();
      if (input.size() <= 1) {
        return actions.keySet().stream()
          .map(PromptSuggestion::suggest)
          .collect(Collectors.toList());
      }
      String actionName = input.getFirst().asString();
      if (suggestions.containsKey(actionName)) {
        return invokeSuggestion(suggestions.get(actionName), input.size() > 1 ? input.subList(1, input.size()) : Collections.emptyList());
      } else {
        return Collections.emptyList();
      }
    }
  }

  private List<Suggestion> invokeSuggestion(Method suggestionMethod, List<CommandInput> commandInputs) {
    Object[] args = resolveArgs(null, commandInputs, suggestionMethod);
    try {
      return (List<Suggestion>) suggestionMethod.invoke(command, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new UnbelievableException(e);
    }
  }

  private Object[] resolveArgs(CommandContext commandContext, List<CommandInput> commandInputs, Executable executable) {
    Iterator<CommandInput> inputIterator = commandInputs.iterator();
    Supplier<String> inputSupplier = () -> {
      if (inputIterator.hasNext()) {
        return inputIterator.next().asString();
      }
      return null;
    };

    Context context = new Context(parameter -> {
      try {
        Method valueOf = parameter.getType().getDeclaredMethod("valueOf", String.class);
        return valueOf.invoke(null, inputIterator.next().asString());
      } catch (NoSuchMethodException | IllegalAccessException e) {
        return null;
      } catch (InvocationTargetException e) {
        throw new UnbelievableException(e.getTargetException());
      }
    });

    if (commandContext != null) {
      context
        .when(ofType(CommandContext.class), commandContext)
        .when(ofType(Writer.class), commandContext::writer);
    }

    context
      .when(ofType(Preference.class), parameter -> preferences.find(resolvePreferenceId(parameter.getName()))
        .or(() -> preferences.find(inputSupplier.get()))
        .orElseThrow(() -> new UnbelievableException("Preference not found for parameter: " + parameter.getName())))

      .when(annotatedWith(PreferenceValue.class), parameter -> {
        String preferenceId = parameter.getAnnotation(PreferenceValue.class).value();
        if (preferenceId.isBlank()) {
          preferenceId = resolvePreferenceId(parameter.getName());
        }
        return preferences.find(preferenceId)
          .orElseThrow(() -> new UnbelievableException("Preference not found for parameter: " + parameter.getName()))
          .value();
      })

      .when(annotatedWith(ParameterCount.class), commandInputs::size)

      .when(ofType(CommandInput.class), () -> inputIterator.hasNext() ? inputIterator.next() : null)

      .when(annotatedWith(RawInput.class), () -> {
        List<String> args = new ArrayList<>();
        inputIterator.forEachRemaining(i -> args.add(i.asString()));
        return String.join(" ", args);
      })

      .when(ofType(String[].class), () -> {
        List<String> args = new ArrayList<>();
        inputIterator.forEachRemaining(i -> args.add(i.asString()));
        return args.toArray(new String[0]);
      })

      .when(ofType(Paginator.class), () -> new Paginator(preferences, terminal, commandContext))

      .when(ofType(String.class), inputSupplier)

      .when(ofType(Integer.class), () -> inputIterator.hasNext() ? inputIterator.next().asInt().orElse(null) : null)

      .when(ofType(Enum.class), parameter -> {
        String name = inputSupplier.get();
        if (name != null) {
          return Enum.valueOf((Class<? extends Enum>) parameter.getType(), resolveEnumName(name));
        }
        return null;
      });

    return context.resolve(executable.getParameters());
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
  public List<String> aliases() {
    return List.of(definition.aliases());
  }

}

package com.backpackcloud.cli.commands;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.CommandInput;
import com.backpackcloud.cli.Displayable;
import com.backpackcloud.cli.EventBus;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.annotations.Action;
import com.backpackcloud.cli.annotations.CommandDefinition;
import com.backpackcloud.cli.annotations.Event;
import com.backpackcloud.cli.annotations.InputParameter;
import com.backpackcloud.cli.annotations.Paginate;
import com.backpackcloud.cli.annotations.ParameterCount;
import com.backpackcloud.cli.annotations.ParameterSuggestion;
import com.backpackcloud.cli.annotations.ParameterSuggestions;
import com.backpackcloud.cli.annotations.PreferenceValue;
import com.backpackcloud.cli.ui.Paginator;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.components.PromptSuggestion;
import com.backpackcloud.preferences.Preference;
import com.backpackcloud.preferences.UserPreferences;
import com.backpackcloud.reflection.Context;
import com.backpackcloud.reflection.Mirror;
import com.backpackcloud.reflection.predicates.ParameterPredicates;
import com.backpackcloud.text.InputValue;
import org.jline.terminal.Terminal;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.backpackcloud.reflection.predicates.MethodPredicates.annotatedWith;
import static com.backpackcloud.reflection.predicates.ParameterPredicates.ofType;

public class AnnotatedCommand implements Command {

  private final CommandDefinition definition;

  private final Object command;

  private final EventBus eventBus;

  private final UserPreferences preferences;
  private final Terminal terminal;

  private final Map<String, CommandAction> actions;
  private final Map<String, Method> suggestions;

  public AnnotatedCommand(Object command,
                          EventBus eventBus,
                          UserPreferences preferences,
                          Terminal terminal) {
    Class<?> commandClass = command.getClass();
    if (!commandClass.isAnnotationPresent(CommandDefinition.class)) {
      throw new UnbelievableException("Command is not annotated with @CommandDefinition");
    }
    this.command = command;
    this.eventBus = eventBus;
    this.preferences = preferences;
    this.terminal = terminal;
    this.definition = commandClass.getAnnotation(CommandDefinition.class);
    this.actions = new HashMap<>();
    this.suggestions = new HashMap<>();
    initialize();
    initializeSuggestions();
  }

  private void initialize() {
    Mirror.reflect(command).methods().stream()
      .filter(annotatedWith(Action.class))
      .forEach(actionMethod -> {
        List<String> inputParameters = resolveInputParameters(actionMethod);
        String name = actionMethod.getAnnotation(Action.class).value();
        if (name.isEmpty()) {
          name = actionMethod.getName();
        }
        actions.put(name, new CommandAction(name, actionMethod, inputParameters));
      });
  }

  private void initializeSuggestions() {
    Mirror.reflect(command).methods().stream()
      .filter(annotatedWith(ParameterSuggestions.class).or(annotatedWith(ParameterSuggestion.class)))
      .forEach(suggestionMethod -> {
        ParameterSuggestion[] annotations = suggestionMethod.getAnnotationsByType(ParameterSuggestion.class);
        for (ParameterSuggestion suggestion : annotations) {
          List<String> actionNames = new ArrayList<>();

          if (suggestion.action().isEmpty()) {
            actionNames.addAll(actions.keySet());
          } else {
            actionNames.add(suggestion.action());
          }

          if (suggestion.parameter().isEmpty()) {
            actionNames.forEach(actionName ->
              actions.get(actionName).inputParameters.forEach(param -> {
                suggestions.put(String.format("%s.%s", actionName, param), suggestionMethod);
              }));
          } else {
            actionNames.forEach(actionName ->
              suggestions.put(String.format("%s.%s", actionName, suggestion.parameter()), suggestionMethod)
            );
          }
        }
      });
  }

  @Override
  public String type() {
    return definition.type();
  }

  @Override
  public String name() {
    return definition.name();
  }

  @Override
  public String description() {
    return definition.description();
  }

  @Override
  public void execute(CommandContext context) {
    List<InputValue> input = context.input().words();
    // no need to check anything if there's only one action
    if (actions.size() == 1) {
      invokeAction(context, actions.values().iterator().next().actionMethod, input);
    } else {
      if (input.isEmpty()) {
        throw new UnbelievableException("No action given");
      }
      String actionName = input.getFirst().get();
      if (actions.containsKey(actionName)) {
        invokeAction(context, actions.get(actionName).actionMethod, input.size() > 1 ? input.subList(1, input.size()) : Collections.emptyList());
      } else {
        throw new UnbelievableException("Action " + actionName + " not recognized");
      }
    }
  }

  @Override
  public List<Suggestion> suggest(CommandInput commandInput) {
    List<InputValue> inputWords = new ArrayList<>(commandInput.words());
    String actionName;
    if (actions.size() == 1) {
      actionName = actions.keySet().iterator().next();
    } else {
      actionName = inputWords.removeFirst().get();
    }
    if (actions.containsKey(actionName)) {
      CommandAction commandAction = actions.get(actionName);
      List<String> commandParameters = commandAction.inputParameters;
      if (!commandParameters.isEmpty() && !inputWords.isEmpty()) {
        int parameterIndex = inputWords.size() - 1;
        // if the last parameter is an array, the index will overflow the actual list of names
        String parameter = commandParameters.get(Math.min(commandParameters.size() - 1, parameterIndex));
        String key = String.format("%s.%s", actionName, parameter);
        if (suggestions.containsKey(key)) {
          return invokeSuggestion(commandAction.actionMethod, suggestions.get(key), inputWords);
        }
      }
    } else if (actions.size() > 1) {
      return actions.keySet().stream()
        .map(PromptSuggestion::suggest)
        .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private List<String> resolveInputParameters(Method actionMethod) {
    return Stream.of(actionMethod.getParameters())
      .filter(ParameterPredicates.annotatedWith(InputParameter.class))
      .map(parameter -> {
        String name = parameter.getAnnotation(InputParameter.class).value();
        if (name.isEmpty()) {
          return parameter.getName();
        }
        return name;
      })
      .collect(Collectors.toList());
  }

  private void invokeAction(CommandContext commandContext, Method actionMethod, List<InputValue> words) {
    Object[] args = resolveArgs(commandContext, Collections.emptyMap(), words, actionMethod);

    Object returnValue;

    try {
      returnValue = actionMethod.invoke(command, args);
      if (actionMethod.isAnnotationPresent(Event.class)) {
        String eventName = actionMethod.getAnnotation(Event.class).value();
        eventBus.send(eventName);
      }
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

  private Object[] resolveArgs(CommandContext commandContext, Map<String, Object> valuesMap, List<InputValue> commandInputs, Executable executable) {
    Iterator<InputValue> inputIterator = commandInputs.iterator();
    Supplier<String> inputSupplier = () -> {
      if (inputIterator.hasNext()) {
        return inputIterator.next().get();
      }
      return null;
    };

    Context context = new Context(parameter -> {
      try {
        Method valueOf = parameter.getType().getDeclaredMethod("valueOf", String.class);
        return valueOf.invoke(null, inputIterator.next().get());
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

    valuesMap.forEach((key, value) ->
      context.when(parameter -> parameter.isAnnotationPresent(InputParameter.class) &&
          (parameter.getAnnotation(InputParameter.class).value().equals(key) ||
            (parameter.getAnnotation(InputParameter.class).value().isEmpty() && parameter.getName().equals(key))),
        parameter -> value));

    context
      .when(ofType(Preference.class), parameter -> preferences.find(resolvePreferenceId(parameter.getName()))
        .or(() -> preferences.find(inputSupplier.get()))
        .orElseThrow(() -> new UnbelievableException("Preference not found for parameter: " + parameter.getName())))

      .when(ParameterPredicates.annotatedWith(PreferenceValue.class), parameter -> {
        String preferenceId = parameter.getAnnotation(PreferenceValue.class).value();
        if (preferenceId.isBlank()) {
          preferenceId = resolvePreferenceId(parameter.getName());
        }
        return preferences.find(preferenceId)
          .orElseThrow(() -> new UnbelievableException("Preference not found for parameter: " + parameter.getName()))
          .value();
      })

      .when(ParameterPredicates.annotatedWith(ParameterCount.class), commandInputs::size)

      .when(ofType(CommandInput.class), () -> inputIterator.hasNext() ? inputIterator.next() : null)

      .when(ofType(String[].class), () -> {
        List<String> args = new ArrayList<>();
        inputIterator.forEachRemaining(i -> args.add(i.get()));
        return args.toArray(new String[0]);
      })

      .when(ofType(Paginator.class), () -> new Paginator(preferences, terminal, commandContext))

      .when(ofType(String.class), inputSupplier)

      .when(ofType(Integer.class), () -> inputIterator.hasNext() ? inputIterator.next().asInteger().orElse(null) : null)

      .when(ofType(InputValue.class), parameter -> InputValue.of(inputIterator))

      .when(ofType(Enum.class), parameter -> InputValue.of(inputSupplier).asEnum((Class<? extends Enum>) parameter.getType()).orElse(null));

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

  private List<Suggestion> invokeSuggestion(Method actionMethod, Method suggestionMethod, List<InputValue> commandInputs) {
    Map<String, Object> inputArguments = new HashMap<>();

    Object[] possibleCommandArgs = resolveArgs(null, Collections.emptyMap(), commandInputs, actionMethod);
    Parameter[] parameters = actionMethod.getParameters();

    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].isAnnotationPresent(InputParameter.class)) {
        Object value = possibleCommandArgs[i];
        if (value != null) {
          String name = parameters[i].getAnnotation(InputParameter.class).value();
          if (name.isEmpty()) {
            name = parameters[i].getName();
          }
          inputArguments.put(name, value);
        }
      }
    }

    Object[] args = resolveArgs(null, inputArguments, commandInputs, suggestionMethod);

    try {
      return (List<Suggestion>) suggestionMethod.invoke(command, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new UnbelievableException(e);
    }
  }

  record CommandAction(String name, Method actionMethod, List<String> inputParameters) {

  }

}

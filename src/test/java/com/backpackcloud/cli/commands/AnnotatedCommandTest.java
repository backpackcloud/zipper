package com.backpackcloud.cli.commands;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.CommandInput;
import com.backpackcloud.cli.EventBus;
import com.backpackcloud.cli.Preferences;
import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.Theme;
import com.backpackcloud.io.SerialBitter;
import com.backpackcloud.preferences.UserPreferences;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

public class AnnotatedCommandTest {

  private final AnnotatedCommand themeCommand = new AnnotatedCommand(
    new ThemeCommand(Theme.create(SerialBitter.YAML())),
    new EventBus(),
    createUserPreferences(),
    createTerminal()
  );

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

  @Test
  public void testThemeActionSuggestions() {
    ParsedLine parsedLine = Mockito.mock(ParsedLine.class);
    when(parsedLine.words()).thenReturn(List.of("theme", ""));
    CommandInput input = new CommandInput(parsedLine);
    List<Suggestion> suggestions = themeCommand.suggest(input);

    assertEquals(6, suggestions.size());
  }

  @Test
  public void testStyleActionParameterSuggestions() {
    ParsedLine parsedLine = Mockito.mock(ParsedLine.class);
    when(parsedLine.words()).thenReturn(List.of("theme", "style", ""));
    CommandInput input = new CommandInput(parsedLine);
    List<Suggestion> suggestions = themeCommand.suggest(input);

    assertFalse(suggestions.isEmpty());
  }

  @Test
  public void testColorActionFirstParameterSuggestions() {
    ParsedLine parsedLine = Mockito.mock(ParsedLine.class);
    when(parsedLine.words()).thenReturn(List.of("theme", "color", ""));
    CommandInput input = new CommandInput(parsedLine);
    List<Suggestion> suggestions = themeCommand.suggest(input);

    assertFalse(suggestions.isEmpty());
  }

  @Test
  public void testColorActionSecondParameterSuggestions() {
    ParsedLine parsedLine = Mockito.mock(ParsedLine.class);
    when(parsedLine.words()).thenReturn(List.of("theme", "color", "red", ""));
    CommandInput input = new CommandInput(parsedLine);
    List<Suggestion> suggestions = themeCommand.suggest(input);

    assertFalse(suggestions.isEmpty());
  }

}

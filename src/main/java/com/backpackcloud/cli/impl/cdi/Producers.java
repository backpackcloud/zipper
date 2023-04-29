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

package com.backpackcloud.cli.impl.cdi;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.AnnotatedCommand;
import com.backpackcloud.cli.CLI;
import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandListener;
import com.backpackcloud.cli.CommandNotifier;
import com.backpackcloud.cli.ErrorRegistry;
import com.backpackcloud.cli.Segments;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.Writers;
import com.backpackcloud.cli.impl.AnnotatedCommandAdapter;
import com.backpackcloud.cli.impl.BaseCLI;
import com.backpackcloud.cli.impl.DefaultCommandNotifier;
import com.backpackcloud.cli.preferences.UserPreferences;
import com.backpackcloud.cli.preferences.impl.UserPreferencesImpl;
import com.backpackcloud.cli.ui.ColorMap;
import com.backpackcloud.cli.ui.IconMap;
import com.backpackcloud.cli.ui.PromptWriter;
import com.backpackcloud.cli.ui.StyleMap;
import com.backpackcloud.cli.ui.Theme;
import com.backpackcloud.cli.ui.impl.DefaultColorMap;
import com.backpackcloud.cli.ui.impl.DefaultIconMap;
import com.backpackcloud.cli.ui.impl.DefaultStyleMap;
import com.backpackcloud.cli.ui.impl.DefaultTheme;
import com.backpackcloud.configuration.Configuration;
import com.backpackcloud.configuration.ResourceConfiguration;
import com.backpackcloud.configuration.UserConfigurationLoader;
import com.backpackcloud.serializer.JSON;
import com.backpackcloud.serializer.Serializer;
import com.backpackcloud.serializer.YAML;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.arc.DefaultBean;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class Producers {

  @Segments
  private class Defaults {

  }

  @Dependent
  @Produces
  @DefaultBean
  public CLI createCLI(InjectionPoint point,
                       Instance<Command> commands,
                       Instance<AnnotatedCommand> annotatedCommands,
                       Instance<PromptWriter> promptWriters,
                       Terminal terminal,
                       CommandNotifier commandNotifier,
                       UserPreferences preferences,
                       Theme theme,
                       Writer consoleWriter) {
    Map<String, PromptWriter> writers = new HashMap<>();
    promptWriters.forEach(w -> writers.put(w.name(), w));

    Annotated annotated = point.getAnnotated();
    Segments segments = annotated.isAnnotationPresent(Segments.class) ?
      annotated.getAnnotation(Segments.class) : Defaults.class.getAnnotation(Segments.class);

    List<PromptWriter> leftPromptWriters = Arrays.stream(segments.left())
      .map(writers::get)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    List<PromptWriter> rightPromptWriters = Arrays.stream(segments.right())
      .map(writers::get)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    List<Command> cliCommands = commands.stream()
      .collect(Collectors.toCollection(ArrayList::new));

    annotatedCommands.stream()
      .map(command -> new AnnotatedCommandAdapter(command, preferences, terminal))
      .forEach(cliCommands::add);

    return new BaseCLI(
      terminal,
      preferences,
      theme,
      cliCommands,
      consoleWriter,
      leftPromptWriters,
      rightPromptWriters,
      commandNotifier
    );
  }

  @Singleton
  @Produces
  @DefaultBean
  public CommandNotifier createCommandNotifier(ErrorRegistry errorRegistry) {
    return new DefaultCommandNotifier(errorRegistry);
  }

  @Singleton
  @Produces
  @DefaultBean
  public CommandListener createCommandListener(CommandNotifier notifier) {
    return notifier.listener();
  }

  @Singleton
  @Produces
  @DefaultBean
  public Writer createConsoleWriter(Theme theme) {
    return Writers.forPrintStream(System.out, theme);
  }

  @Singleton
  @Produces
  @DefaultBean
  public Theme createTheme(ColorMap colorMap, IconMap iconMap, StyleMap styleMap) {
    return new DefaultTheme(colorMap, iconMap, styleMap);
  }

  @Singleton
  @Produces
  @DefaultBean
  public ColorMap createColorMap(@YAML Serializer serializer) {
    return new DefaultColorMap(loadMap(serializer, "colors"));
  }

  @Singleton
  @Produces
  @DefaultBean
  public IconMap createIconMap(@YAML Serializer serializer) {
    return new DefaultIconMap(loadMap(serializer, "icons"));
  }

  @Singleton
  @Produces
  @DefaultBean
  public StyleMap createStyleMap(@YAML Serializer serializer) {
    return new DefaultStyleMap(loadMap(serializer, "styles"));
  }

  private Map<String, String> loadMap(Serializer serializer, String mapName) {
    Configuration configuration = new ResourceConfiguration("META-INF/zipper/" + mapName + ".yml");
    Map<String, String> map = serializer.deserialize(configuration.read(), HashMap.class);

    Configuration extraMap = new ResourceConfiguration("META-INF/config/" + mapName + ".yml");
    if (extraMap.isSet()) {
      map.putAll(serializer.deserialize(extraMap.read(), HashMap.class));
    }
    return map;
  }

  @Singleton
  @Produces
  @DefaultBean
  public Terminal createTerminal() {
    try {
      return TerminalBuilder.terminal();
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  @Singleton
  @Produces
  @DefaultBean
  public UserPreferences createUserPreferences(@YAML Serializer serializer) {
    UserConfigurationLoader loader = new UserConfigurationLoader("zipper");
    UserPreferences preferences = new UserPreferencesImpl();

    loader.resolve()
      .filter(Configuration::isSet)
      .map(Configuration::read)
      .map(configuration -> serializer.deserialize(configuration, FilePreferences.class))
      .map(FilePreferences::preferences)
      .ifPresent(preferences::load);

    return preferences;
  }

  private Serializer json;
  private Serializer yaml;

  @Produces
  public Serializer createSerializer(InjectionPoint ip) {
    if (ip.getAnnotated().isAnnotationPresent(JSON.class)) {
      if (json == null) {
        json = Serializer.json();
      }
      return json;
    }
    if (ip.getAnnotated().isAnnotationPresent(YAML.class)) {
      if (yaml == null) {
        yaml = Serializer.yaml();
      }
      return yaml;
    }
    throw new UnbelievableException("Cannot find serializer");
  }

  @RegisterForReflection
  public static class FilePreferences {

    private final Map<String, Configuration> preferences;

    @JsonCreator
    FilePreferences(@JsonProperty("preferences") Map<String, Configuration> preferences) {
      this.preferences = preferences;
    }

    public Map<String, Configuration> preferences() {
      return preferences;
    }

  }

}

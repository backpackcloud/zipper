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

package com.backpackcloud.cli.cdi;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.AnnotatedCommand;
import com.backpackcloud.cli.CLI;
import com.backpackcloud.cli.Command;
import com.backpackcloud.cli.CommandBus;
import com.backpackcloud.cli.PreferenceValue;
import com.backpackcloud.cli.Preferences;
import com.backpackcloud.cli.Segments;
import com.backpackcloud.cli.commands.AnnotatedCommandAdapter;
import com.backpackcloud.cli.ui.ColorMap;
import com.backpackcloud.cli.ui.IconMap;
import com.backpackcloud.cli.ui.PromptWriter;
import com.backpackcloud.cli.ui.StyleMap;
import com.backpackcloud.cli.ui.Theme;
import com.backpackcloud.configuration.Configuration;
import com.backpackcloud.configuration.ConfigurationSupplier;
import com.backpackcloud.configuration.ResourceConfiguration;
import com.backpackcloud.io.SerialBitter;
import com.backpackcloud.preferences.Preference;
import com.backpackcloud.preferences.UserPreferences;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.arc.DefaultBean;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Singleton;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

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

  private final SerialBitter serializer = SerialBitter.YAML();

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
                       CommandBus commandBus,
                       UserPreferences preferences,
                       Theme theme) {
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

    return new CLI(
      terminal,
      preferences,
      theme,
      cliCommands,
      leftPromptWriters,
      rightPromptWriters,
      commandBus
    );
  }

  @Produces
  public Preference getPreference(InjectionPoint ip, UserPreferences preferences) {
    PreferenceValue annotation = ip.getAnnotated().getAnnotation(PreferenceValue.class);
    String preferenceName = annotation.value().isEmpty() ? ip.getMember().getName() : annotation.value();

    return preferences.find(preferenceName)
      .orElseThrow(UnbelievableException.because("Preference not found: " + preferenceName));
  }

  @Singleton
  @Produces
  @DefaultBean
  public Theme createTheme(ColorMap colorMap, IconMap iconMap, StyleMap styleMap) {
    return new Theme(colorMap, iconMap, styleMap);
  }

  @Singleton
  @Produces
  @DefaultBean
  public ColorMap createColorMap() {
    return new ColorMap(loadMap("colors"));
  }

  @Singleton
  @Produces
  @DefaultBean
  public IconMap createIconMap() {
    return new IconMap(loadMap("icons"));
  }

  @Singleton
  @Produces
  @DefaultBean
  public StyleMap createStyleMap(ColorMap colorMap) {
    Map<String, String> styleMap = loadMap("styles");
    colorMap.colors().stream()
      .filter(color -> !styleMap.containsKey(color))
      .forEach(color -> styleMap.put(color, colorMap.valueOf(color)));
    return new StyleMap(styleMap);
  }

  private Map<String, String> loadMap(String mapName) {
    Configuration configuration = new ResourceConfiguration("META-INF/zipper/" + mapName + ".yml");
    Map<String, String> map = serializer.deserialize(configuration.read(), HashMap.class);

    Configuration extraMap = new ResourceConfiguration("META-INF/config/" + mapName + ".yml");
    if (extraMap.isSet()) {
      String content = extraMap.read();
      if (content != null && !content.isEmpty()) {
        map.putAll(serializer.deserialize(extraMap.read(), HashMap.class));
      }
    }
    return map;
  }

  @Singleton
  @Produces
  @DefaultBean
  public Terminal createTerminal() {
    try {
      return TerminalBuilder.builder()
        .system(true)
        .build();
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  @Singleton
  @Produces
  @DefaultBean
  public UserPreferences createUserPreferences() {
    ConfigurationSupplier loader = new ConfigurationSupplier("zipper");
    UserPreferences preferences = new UserPreferences();

    preferences.register(Preferences.class);

    loader.get().ifSet(conf ->
      serializer.deserialize(conf.read(), FilePreferences.class)
        .preferences()
        .forEach((id, configuration) -> {
          preferences
            .find(id)
            .ifPresent(preference -> preference.set(configuration.read()));
        })
    );

    return preferences;
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

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

package com.backpackcloud.cli.ui;

import com.backpackcloud.configuration.Configuration;
import com.backpackcloud.configuration.ResourceConfiguration;
import com.backpackcloud.io.Deserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public record Theme(ColorMap colorMap, IconMap iconMap, StyleMap styleMap) {

  public static Theme create(Deserializer deserializer) {
    Function<String, Map<String, String>> loadMap = name -> {
      Configuration configuration = new ResourceConfiguration("META-INF/zipper/" + name + ".yml");
      Map<String, String> map = deserializer.deserialize(configuration.read(), HashMap.class);

      Configuration extraMap = new ResourceConfiguration("META-INF/config/" + name + ".yml");
      if (extraMap.isSet()) {
        String content = extraMap.read();
        if (content != null && !content.isEmpty()) {
          map.putAll(deserializer.deserialize(extraMap.read(), HashMap.class));
        }
      }
      return map;
    };
    ColorMap colorMap = new ColorMap(loadMap.apply("colors"));
    IconMap iconMap = new IconMap(loadMap.apply("icons"));

    Map<String, String> styleMap = loadMap.apply("styles");
    colorMap.colors().stream()
      .filter(color -> !styleMap.containsKey(color))
      .forEach(color -> styleMap.put(color, colorMap.valueOf(color)));

    return new Theme(colorMap, iconMap, new StyleMap(styleMap));
  }

}

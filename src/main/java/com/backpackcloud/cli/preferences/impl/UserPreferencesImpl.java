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

package com.backpackcloud.cli.preferences.impl;

import com.backpackcloud.cli.preferences.Preference;
import com.backpackcloud.cli.preferences.PreferenceSpec;
import com.backpackcloud.cli.preferences.UserPreference;
import com.backpackcloud.cli.preferences.UserPreferences;
import com.backpackcloud.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserPreferencesImpl implements UserPreferences {

  private final Map<String, Preference> preferencesMap;

  private final Map<String, String> unknownPreferences;

  public UserPreferencesImpl() {
    this.preferencesMap = new ConcurrentHashMap<>();
    this.unknownPreferences = new ConcurrentHashMap<>();
    initPreferences();
  }

  private void initPreferences() {
    for (PreferenceSpec preference : UserPreference.values()) {
      register(preference);
    }
  }

  @Override
  public UserPreferences register(PreferenceSpec spec) {
    if (!preferencesMap.containsKey(spec.id())) {
      Preference<?> preference = new PreferenceImpl<>(spec);
      this.preferencesMap.put(spec.id(), preference);
      // checks if there is an unknown preference stored for this id
      if (this.unknownPreferences.containsKey(spec.id())) {
        preference.set(this.unknownPreferences.remove(spec.id()));
      }
    }
    return this;
  }

  @Override
  public <E> Optional<Preference<E>> find(String id) {
    return Optional.ofNullable(preferencesMap.get(id));
  }

  @Override
  public <E> Preference<E> get(PreferenceSpec spec) {
    if (!preferencesMap.containsKey(spec.id())) {
      preferencesMap.put(spec.id(), new PreferenceImpl(spec));
    }
    return preferencesMap.get(spec.id());
  }

  @Override
  public List<Preference<?>> list() {
    return new ArrayList(preferencesMap.values());
  }

  @Override
  public void load(Map<String, Configuration> preferencesMap) {
    preferencesMap.forEach((key, config) -> {
      if (config.isSet()) {
        if (this.preferencesMap.containsKey(key)) {
          this.preferencesMap.get(key).set(config.get());
        } else {
          // saves the unknown preference so if it's registered later on
          // it can pick the user value
          this.unknownPreferences.put(key, config.get());
        }
      }
    });
  }
}

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

package com.backpackcloud.cli.preferences;

import com.backpackcloud.configuration.Configuration;

import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Defines a container that manages user preferences across the
 * application.
 *
 * @author Marcelo "Ataxexe“ Guimarães
 */
public interface UserPreferences {

  UserPreferences register(PreferenceSpec spec);

  default UserPreferences register(PreferenceSpec... specs) {
    for (PreferenceSpec spec : specs) {
      register(spec);
    }
    return this;
  }

  void load(Map<String, Configuration> preferencesMap);

  /**
   * Finds the preference identified by the given id.
   * <p>
   * Returns an empty value if the preference is not found.
   *
   * @param id the preference's identifier
   * @return the found preference
   * @see PreferenceSpec#id()
   */
  <E> Optional<Preference<E>> find(String id);

  /**
   * Returns the preference specified by the given spec.
   * <p>
   * This method is guaranteed to return a preference object,
   * even if there wasn't any stored at the moment.
   *
   * @param spec the preference's specification
   * @return the managed preference
   */
  <E> Preference<E> get(PreferenceSpec spec);

  /**
   * @return all the preferences currently managed
   */
  List<Preference<?>> list();

  /**
   * Shorten to {@code get(spec).listen(action)}. Assumes a
   * {@link PreferenceSpec.Type#FLAG flag} preference.
   *
   * @param spec   the preference spec
   * @param action the listener action
   * @return a reference to this object
   * @see Preference#listen(Consumer)
   */
  default UserPreferences watchFlag(PreferenceSpec spec, Consumer<Boolean> action) {
    Preference preference = get(spec);
    preference.listen(action);
    return this;
  }

  /**
   * Shorten to {@code get(spec).listen(action)}. Assumes a
   * {@link PreferenceSpec.Type#NUMBER number} preference.
   *
   * @param spec   the preference spec
   * @param action the listener action
   * @return a reference to this object
   * @see Preference#listen(Consumer)
   */
  default UserPreferences watchNumber(PreferenceSpec spec, Consumer<Integer> action) {
    Preference preference = get(spec);
    preference.listen(action);

    return this;
  }

  /**
   * Shorten to {@code get(spec).listen(action)}. Assumes a
   * {@link PreferenceSpec.Type#TEXT text} preference.
   *
   * @param spec   the preference spec
   * @param action the listener action
   * @return a reference to this object
   * @see Preference#listen(Consumer)
   */
  default UserPreferences watchText(PreferenceSpec spec, Consumer<String> action) {
    Preference preference = get(spec);
    preference.listen(action);

    return this;
  }

  /**
   * Returns a supplier that always supplies the current preference value.
   * <p>
   * Assumes a {@link PreferenceSpec.Type#FLAG flag} preference.
   *
   * @param spec the preference spec
   * @return a supplier that returns the preference value.
   */
  default Supplier<Boolean> flag(PreferenceSpec spec) {
    return () -> (Boolean) get(spec).value();
  }

  /**
   * Returns a supplier that always supplies the current preference value.
   * <p>
   * Assumes a {@link PreferenceSpec.Type#TEXT text} preference.
   *
   * @param spec the preference spec
   * @return a supplier that returns the preference value.
   */
  default Supplier<String> text(PreferenceSpec spec) {
    return () -> (String) get(spec).value();
  }

  /**
   * Returns a supplier that always supplies the current preference value.
   * <p>
   * Assumes a {@link PreferenceSpec.Type#NUMBER number} preference.
   *
   * @param spec the preference spec
   * @return a supplier that returns the preference value.
   */
  default Supplier<Integer> number(PreferenceSpec spec) {
    return () -> (Integer) get(spec).value();
  }

  /**
   * Returns a supplier that always supplies the current preference value.
   * <p>
   * Assumes a {@link PreferenceSpec.Type#CHARSET charset} preference.
   *
   * @param spec the preference spec
   * @return a supplier that returns the preference value.
   */
  default Supplier<Charset> charset(PreferenceSpec spec) {
    return () -> (Charset) get(spec).value();
  }

  /**
   * Returns a supplier that always supplies the current preference value.
   * <p>
   * Assumes a {@link PreferenceSpec.Type#TIMESTAMP_FORMAT timestamp format}
   * preference.
   *
   * @param spec the preference spec
   * @return a supplier that returns the preference value.
   */
  default Supplier<DateTimeFormatter> timestampFormatter(PreferenceSpec spec) {
    return () -> (DateTimeFormatter) get(spec).value();
  }

  /**
   * Checks if the given {@link PreferenceSpec.Type#FLAG flag} preference
   * is enabled.
   *
   * @param spec the preference spec
   * @return {@code true} if the related preference holds a {@code true} value.
   */
  default boolean isEnabled(PreferenceSpec spec) {
    return flag(spec).get();
  }

  /**
   * Checks if the given {@link PreferenceSpec.Type#FLAG flag} preference
   * is disabled.
   *
   * @param spec the preference spec
   * @return {@code true} if the related preference holds a {@code false} value.
   */
  default boolean isDisabled(PreferenceSpec spec) {
    return !isEnabled(spec);
  }

}

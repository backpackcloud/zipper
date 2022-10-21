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

package com.backpackcloud.text;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpolator {

  public static final Pattern SINGLE_BRACKET_INTERPOLATION_PATTERN = Pattern.compile("\\{(?<token>[^}]+)}");

  public static final Pattern DOUBLE_BRACKET_INTERPOLATION_PATTERN = Pattern.compile("\\{\\{(?<token>[^}]+)}}");
  public static final Pattern DEFAULT_INTERPOLATION_PATTERN = SINGLE_BRACKET_INTERPOLATION_PATTERN;

  private final Pattern pattern;
  private final Function<String, Object> tokenResolver;

  public Interpolator(Function<String, Object> tokenResolver) {
    this(DEFAULT_INTERPOLATION_PATTERN, tokenResolver);
  }

  public Interpolator(Pattern pattern,
                      Function<String, Object> tokenResolver) {
    this.pattern = pattern;
    this.tokenResolver = tokenResolver;
  }

  public Optional<String> eval(String value) {
    if (value == null || value.isBlank()) return Optional.empty();

    StringBuilder result = new StringBuilder();
    Matcher matcher = pattern.matcher(value);

    int pos = 0;
    while (matcher.find()) {
      String token = matcher.group("token");
      String format = "";
      int indexOfPipe = token.indexOf("|");

      if (indexOfPipe > 0) {
        format = token.substring(indexOfPipe + 1);
        token = token.substring(0, indexOfPipe);
      }

      Object resolvedValue = tokenResolver.apply(token);
      if (resolvedValue == null) {
        return Optional.empty();
      }
      result.append(value, pos, matcher.start()).append(String.format("%" + format + "s", resolvedValue));
      pos = matcher.end();
    }

    if (pos < value.length()) {
      result.append(value.substring(pos));
    }

    return Optional.of(result.toString());
  }

  public static Interpolator from(Map<String, Object> context) {
    return new Interpolator(context::get);
  }

}

package com.backpackcloud.zipper;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpolator implements Function<String, String> {

  public static final Pattern DEFAULT_INTERPOLATION_PATTERN = Pattern.compile("\\{(?<token>[^}]+)}");

  private final Pattern patter;
  private final Function<String, Object> tokenResolver;

  public Interpolator(Function<String, Object> tokenResolver) {
    this(DEFAULT_INTERPOLATION_PATTERN, tokenResolver);
  }

  public Interpolator(Pattern pattern, Function<String, Object> tokenResolver) {
    this.patter = pattern;
    this.tokenResolver = tokenResolver;
  }

  @Override
  public String apply(String value) {
    if (value == null || value.isBlank()) return null;

    StringBuilder result = new StringBuilder();
    Matcher matcher = patter.matcher(value);

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

      result.append(value, pos, matcher.start()).append(String.format("%" + format + "s", resolvedValue));
      pos = matcher.end();
    }

    if (pos < value.length()) {
      result.append(value.substring(pos));
    }

    return result.toString();
  }

}

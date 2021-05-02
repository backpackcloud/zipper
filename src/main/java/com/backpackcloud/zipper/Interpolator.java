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

    StringBuilder result = new StringBuilder(value);
    Matcher matcher = patter.matcher(result);

    while (matcher.find()) {
      Object tokenValue = tokenResolver.apply(matcher.group("token"));
      result.replace(matcher.start(), matcher.end(), String.valueOf(tokenValue));
    }

    return result.toString();
  }

}

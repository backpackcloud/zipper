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

package com.backpackcloud.cli.ui.impl;

import com.backpackcloud.cli.ui.Color;
import com.backpackcloud.cli.ui.ColorMap;
import com.backpackcloud.cli.ui.Stylish;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import java.util.Optional;
import java.util.function.Function;

public class StyleBuilder<R> implements Stylish<R> {

  private final AttributedStyle initialStyle;
  private final ColorMap colorMap;
  private final Function<AttributedStyle, R> returnValueFunction;

  private AttributedStyle style;

  public StyleBuilder(AttributedStyle initialStyle,
                      ColorMap colorMap,
                      Function<AttributedStyle, R> returnValueFunction) {
    this.initialStyle = initialStyle;
    this.colorMap = colorMap;
    this.style = initialStyle;
    this.returnValueFunction = returnValueFunction;
  }

  @Override
  public Stylish<R> foreground(String foreground) {
    Optional<Color> color = colorMap.colorOf(foreground);
    color.ifPresent(value -> style = style.foregroundRgb(value.toInt()));
    return this;
  }

  @Override
  public Stylish<R> background(String background) {
    Optional<Color> color = colorMap.colorOf(background);
    color.ifPresent(value -> style = style.backgroundRgb(value.toInt()));
    return this;
  }

  @Override
  public Stylish<R> bold() {
    style = style.bold();
    return this;
  }

  @Override
  public Stylish<R> boldOff() {
    style = style.boldOff();
    return this;
  }

  @Override
  public Stylish<R> italic() {
    style = style.italic();
    return this;
  }

  @Override
  public Stylish<R> italicOff() {
    style = style.italicOff();
    return this;
  }

  @Override
  public Stylish<R> underline() {
    style = style.underline();
    return this;
  }

  @Override
  public Stylish<R> underlineOff() {
    style = style.underlineOff();
    return this;
  }

  @Override
  public Stylish<R> blink() {
    style = style.blink();
    return this;
  }

  @Override
  public Stylish<R> blinkOff() {
    style = style.blinkOff();
    return this;
  }

  @Override
  public Stylish<R> crossedOut() {
    style = style.crossedOut();
    return this;
  }

  @Override
  public Stylish<R> crossedOutOff() {
    style = style.crossedOutOff();
    return this;
  }

  @Override
  public Stylish<R> parse(String styleString) {
    String[] parts = styleString.split("/");
    String foreground = parts[0];
    String background = parts.length > 1 ? parts[1] : "";
    String options = parts.length > 2 ? parts[2] : "";

    if (!foreground.isBlank()) foreground(foreground);
    if (!background.isBlank()) background(background);
    if (options.contains("b")) bold();
    if (options.contains("i")) italic();
    if (options.contains("u")) underline();
    if (options.contains("k")) blink();
    if (options.contains("c")) crossedOut();

    return this;
  }

  @Override
  public R reset() {
    return returnValueFunction.apply(initialStyle);
  }

  @Override
  public R set() {
    return returnValueFunction.apply(style);
  }

  public static Stylish<AttributedStyle> newSimpleBuilder(ColorMap colorMap) {
    return new StyleBuilder<>(AttributedStyle.DEFAULT, colorMap, Function.identity());
  }

  public static Stylish<Function<String, String>> newFormatterBuilder(ColorMap colorMap) {
    return new StyleBuilder<>(AttributedStyle.DEFAULT, colorMap, attributedStyle -> text ->
      new AttributedString(text, attributedStyle).toAnsi());
  }

}

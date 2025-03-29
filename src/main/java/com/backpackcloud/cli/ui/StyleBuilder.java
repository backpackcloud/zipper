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

import org.jline.utils.AttributedStyle;

import java.util.Optional;
import java.util.function.Function;

public class StyleBuilder<R> {

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

  public StyleBuilder<R> foreground(String foreground) {
    Optional<Color> color = colorMap.colorOf(foreground);
    color.ifPresent(value -> style = style.foregroundRgb(value.toInt()));
    return this;
  }

  public StyleBuilder<R> background(String background) {
    Optional<Color> color = colorMap.colorOf(background);
    color.ifPresent(value -> style = style.backgroundRgb(value.toInt()));
    return this;
  }

  public StyleBuilder<R> bold() {
    style = style.bold();
    return this;
  }

  public StyleBuilder<R> boldOff() {
    style = style.boldOff();
    return this;
  }

  public StyleBuilder<R> italic() {
    style = style.italic();
    return this;
  }

  public StyleBuilder<R> italicOff() {
    style = style.italicOff();
    return this;
  }

  public StyleBuilder<R> underline() {
    style = style.underline();
    return this;
  }

  public StyleBuilder<R> underlineOff() {
    style = style.underlineOff();
    return this;
  }

  public StyleBuilder<R> blink() {
    style = style.blink();
    return this;
  }

  public StyleBuilder<R> blinkOff() {
    style = style.blinkOff();
    return this;
  }

  public StyleBuilder<R> crossedOut() {
    style = style.crossedOut();
    return this;
  }

  public StyleBuilder<R> crossedOutOff() {
    style = style.crossedOutOff();
    return this;
  }

  public StyleBuilder<R> parse(String styleString) {
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

  public R reset() {
    return returnValueFunction.apply(initialStyle);
  }

  public R set() {
    return returnValueFunction.apply(style);
  }

  public static StyleBuilder<AttributedStyle> newSimpleBuilder(ColorMap colorMap) {
    return new StyleBuilder<>(AttributedStyle.DEFAULT, colorMap, Function.identity());
  }

}

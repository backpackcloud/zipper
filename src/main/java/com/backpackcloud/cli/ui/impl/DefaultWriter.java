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

import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.ui.Stylish;
import com.backpackcloud.cli.ui.Theme;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class DefaultWriter implements Writer {

  private final Theme theme;
  private final AttributedStyle style;
  private final BiFunction<String, AttributedStyle, AttributedString> createTextFunction;
  private final Consumer<AttributedString> delegate;

  public DefaultWriter(Theme theme,
                       AttributedStyle style,
                       BiFunction<String, AttributedStyle, AttributedString> createTextFunction,
                       Consumer<AttributedString> delegate) {
    this.theme = theme;
    this.style = style;
    this.createTextFunction = createTextFunction;
    this.delegate = delegate;
  }

  @Override
  public Stylish<Writer> style() {
    return new StyleBuilder<>(
      AttributedStyle.DEFAULT,
      theme.colorMap(),
      newStyle -> new DefaultWriter(theme, newStyle, createTextFunction, delegate)
    );
  }

  @Override
  public Writer withStyle(AttributedStyle newStyle) {
    return new DefaultWriter(theme, newStyle, createTextFunction, delegate);
  }

  @Override
  public Writer withStyle(String style) {
    String mappedStyle = theme.styleMap().styleOf(style);
    return style().parse(mappedStyle != null ? mappedStyle : style).set();
  }

  @Override
  public Writer withStyle(String... styles) {
    return Arrays.stream(styles)
      .map(theme.styleMap()::styleOf)
      .filter(Objects::nonNull)
      .findFirst()
      .map(style -> style().parse(style).set())
      .orElse(this);
  }

  @Override
  public Writer write(String text) {
    return writeText(text);
  }

  @Override
  public Writer writeIcon(String icon) {
    return writeText(theme.iconMap().symbolOf(icon));
  }

  @Override
  public Writer writeln(String text) {
    return write(text).newLine();
  }

  private Writer writeText(String text) {
    delegate.accept(createTextFunction.apply(text, style));
    return this;
  }

}

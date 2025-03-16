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

package com.backpackcloud.cli;

import com.backpackcloud.cli.ui.Theme;
import com.backpackcloud.cli.ui.StyleBuilder;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Writer {

  private final Theme theme;
  private final AttributedStyle style;
  private final BiFunction<String, AttributedStyle, AttributedString> createTextFunction;
  private final Consumer<AttributedString> delegate;
  private final Terminal terminal;

  public Writer(Theme theme,
                       AttributedStyle style,
                       BiFunction<String, AttributedStyle, AttributedString> createTextFunction,
                       Consumer<AttributedString> delegate,
                       Terminal terminal) {
    this.theme = theme;
    this.style = style;
    this.createTextFunction = createTextFunction;
    this.delegate = delegate;
    this.terminal = terminal;
  }

  public StyleBuilder<Writer> style() {
    return new StyleBuilder<>(
      AttributedStyle.DEFAULT,
      theme.colorMap(),
      newStyle -> new Writer(theme, newStyle, createTextFunction, delegate, terminal)
    );
  }

  public Writer withStyle(AttributedStyle newStyle) {
    return new Writer(theme, newStyle, createTextFunction, delegate, terminal);
  }

  public Writer withStyle(String style) {
    String mappedStyle = theme.styleMap().styleOf(style);
    return style().parse(mappedStyle != null ? mappedStyle : style).set();
  }

  public Writer withStyle(String... styles) {
    return Arrays.stream(styles)
      .map(theme.styleMap()::styleOf)
      .filter(Objects::nonNull)
      .findFirst()
      .map(style -> style().parse(style).set())
      .orElse(this);
  }

  public Writer write(String text) {
    return writeText(text);
  }

  public Writer writeIcon(String icon) {
    return writeText(theme.iconMap().symbolOf(icon));
  }

  public Writer writeln(String text) {
    return write(text).newLine();
  }

  public Writer newLine() {
    write("\n");
    terminal.flush();
    return this;
  }

  private Writer writeText(String text) {
    delegate.accept(createTextFunction.apply(text, style));
    return this;
  }

  public Writer write(Object object) {
    return write(String.valueOf(object));
  }

  public Writer write(int value) {
    return write(String.valueOf(value));
  }

  public Writer write(long value) {
    return write(String.valueOf(value));
  }

  public Writer write(Displayable displayable) {
    displayable.toDisplay(this);
    return this;
  }

  public Writer writeln(Displayable displayable) {
    displayable.toDisplay(this);
    return this.newLine();
  }

}

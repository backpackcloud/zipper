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

package com.backpackcloud.cli.impl;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.ui.Stylish;
import com.backpackcloud.cli.ui.Theme;
import com.backpackcloud.cli.ui.impl.StyleBuilder;
import org.jline.utils.AttributedStyle;

import java.io.BufferedWriter;
import java.io.IOException;

public class OutputRedirectWriter implements Writer {

  private final Theme theme;
  private final BufferedWriter writer;

  public OutputRedirectWriter(Theme theme, BufferedWriter writer) {
    this.theme = theme;
    this.writer = writer;
  }

  @Override
  public Writer withStyle(AttributedStyle style) {
    return this;
  }

  @Override
  public Writer withStyle(String style) {
    return this;
  }

  @Override
  public Writer withStyle(String... styles) {
    return this;
  }

  @Override
  public Stylish<Writer> style() {
    return new StyleBuilder<>(AttributedStyle.DEFAULT, theme.colorMap(), newStyle -> this);
  }

  @Override
  public Writer writeIcon(String icon) {
    return write(theme.iconMap().symbolOf(icon));
  }

  @Override
  public Writer write(String text) {
    try {
      writer.write(text);
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
    return this;
  }

  @Override
  public Writer writeln(String text) {
    try {
      writer.write(text);
      writer.newLine();
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
    return this;
  }

}

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

import com.backpackcloud.cli.ui.Stylish;
import org.jline.utils.AttributedStyle;

/**
 * Interface that defines a component for writing text with colors
 * in the output.
 * <p>
 * Writers can target any kind of outputs. A console out, a file or
 * even a String.
 *
 * @author Marcelo “Ataxexe" Guimarães
 */
public interface Writer {

  Stylish<Writer> style();

  Writer withStyle(AttributedStyle style);

  Writer withStyle(String style);

  Writer withStyle(String... styles);

  /**
   * Writes the given text to the output target.
   *
   * @param text the text to write.
   * @return a reference to this instance.
   */
  Writer write(String text);

  default Writer write(Object object) {
    return write(String.valueOf(object));
  }

  default Writer write(int value) {
    return write(String.valueOf(value));
  }

  default Writer write(long value) {
    return write(String.valueOf(value));
  }

  Writer writeIcon(String icon);

  default Writer write(Displayable displayable) {
    displayable.toDisplay(this);
    return this;
  }

  default Writer writeln(Displayable displayable) {
    displayable.toDisplay(this);
    return this.newLine();
  }

  /**
   * Writes the given text to the output target, followed by a line break.
   *
   * @param text the text to write.
   * @return a reference to this instance.
   */
  Writer writeln(String text);

  /**
   * Writes a line break to the output target.
   *
   * @return a reference to this instance.
   */
  default Writer newLine() {
    return write("\n");
  }

}

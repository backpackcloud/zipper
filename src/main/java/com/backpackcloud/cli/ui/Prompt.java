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

import com.backpackcloud.cli.Displayable;
import com.backpackcloud.cli.Writer;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

public class Prompt {

  private final Writer writer;
  private final Terminal terminal;
  private final Theme theme;

  private final String tail;
  private final String separator;
  private final String head;

  private final String foreground;
  private final String background;

  private boolean isOpened;

  public Prompt(Theme theme,
                Writer writer,
                Terminal terminal,
                String tail,
                String separator,
                String head,
                String foreground,
                String background) {
    this.theme = theme;
    this.writer = writer;
    this.terminal = terminal;
    this.tail = theme.iconMap().symbolOf(tail);
    this.separator = theme.iconMap().symbolOf(separator);
    this.head = theme.iconMap().symbolOf(head);
    this.foreground = foreground;
    this.background = background;
  }

  public PromptSegmentBuilder newSegment() {
    if (isOpened) {
      writer.style()
        .foreground(foreground)
        .background(background)
        .set()
        .write(separator);
    } else {
      isOpened = true;
      writer.style()
        .foreground(background)
        .set().write(tail);
    }

    writer.style()
      .foreground(foreground)
      .background(background)
      .set().write(" ");

    return new PromptSegmentBuilder() {

      private final AttributedStyle style = StyleBuilder
        .newSimpleBuilder(theme.colorMap())
        .foreground(foreground)
        .background(background)
        .set();

      @Override
      public PromptSegmentBuilder add(String text) {
        writer.withStyle(style).write(text).write(" ");
        return this;
      }

      @Override
      public PromptSegmentBuilder add(int i) {
        writer.withStyle(style).write(i).write(" ");
        return this;
      }

      @Override
      public PromptSegmentBuilder add(long l) {
        writer.withStyle(style).write(l).write(" ");
        return this;
      }

      @Override
      public PromptSegmentBuilder add(Displayable object) {
        StringBuilder stringBuilder = new StringBuilder();
        // Ignores any style so it appears flat in the segment
        Writer stringWriter = new Writer(
          theme,
          AttributedStyle.DEFAULT,
          (s, attributedStyle) -> new AttributedString(s, AttributedStyle.DEFAULT),
          text -> stringBuilder.append(text.toAnsi()),
          terminal
        );

        object.toDisplay(stringWriter);

        writer.withStyle(style).write(stringBuilder.toString());

        return this;
      }

      @Override
      public PromptSegmentBuilder addIcon(String icon) {
        writer.withStyle(style).writeIcon(icon).write(" ");
        return this;
      }

      @Override
      public PromptSegmentBuilder addIcon(String icon, String color) {
        AttributedStyle customStyle = StyleBuilder
          .newSimpleBuilder(theme.colorMap())
          .foreground(color)
          .background(background)
          .set();
        writer.withStyle(customStyle).write(theme.iconMap().symbolOf(icon) + " ");
        return this;
      }

      @Override
      public Prompt close() {
        return Prompt.this;
      }
    };
  }

  public Prompt newLine() {
    writer.newLine();
    return this;
  }

  public Writer writer() {
    return writer;
  }

  public void writeIndicator(String style) {
    writer.withStyle(style).write(theme.iconMap().symbolOf("prompt")).write(" ");
  }

  public Prompt closeSegments() {
    if (isOpened) {
      isOpened = false;
      writer.withStyle(foreground + "/" + background).write(" ");
      writer.style()
        .foreground(background)
        .set()
        .write(head);
    }
    return this;
  }

  public static Prompt create(Theme theme, Writer writer, Terminal terminal, String tail, String separator, String head) {
    return new Prompt(theme, writer, terminal, tail, separator, head,
      "prompt-segment-fg", "prompt-segment-bg"
    );
  }

  public interface PromptSegmentBuilder {

    PromptSegmentBuilder add(String text);

    PromptSegmentBuilder add(int i);

    PromptSegmentBuilder add(long l);

    PromptSegmentBuilder add(Displayable object);

    PromptSegmentBuilder addIcon(String icon);

    PromptSegmentBuilder addIcon(String icon, String color);

    Prompt close();

  }

}

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

package com.backpackcloud.cli.ui;

import com.backpackcloud.cli.Displayable;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.ui.impl.PromptBuilder;

public interface Prompt {

  PromptSegmentBuilder newSegment();

  Prompt closeSegments();

  Prompt newLine();

  Writer writer();

  void writeIndicator(String style);

  static Prompt create(Theme theme, Writer writer, String tail, String separator, String head) {
    return new PromptBuilder(theme, writer, tail, separator, head,
      "prompt-segment-fg", "prompt-segment-bg"
    );
  }

  interface PromptSegmentBuilder {

    PromptSegmentBuilder add(String text);

    PromptSegmentBuilder add(int i);

    PromptSegmentBuilder add(long l);

    PromptSegmentBuilder add(Displayable object);

    PromptSegmentBuilder addIcon(String icon);

    PromptSegmentBuilder addIcon(String icon, String color);

    Prompt close();

  }

}

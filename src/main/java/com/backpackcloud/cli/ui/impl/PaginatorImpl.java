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

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.Displayable;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.preferences.UserPreference;
import com.backpackcloud.cli.preferences.UserPreferences;
import com.backpackcloud.cli.ui.Paginator;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

public class PaginatorImpl implements Paginator {

  private final UserPreferences preferences;

  private final Terminal terminal;

  private final CommandContext context;

  public PaginatorImpl(UserPreferences preferences, Terminal terminal, CommandContext context) {
    this.preferences = preferences;
    this.terminal = terminal;
    this.context = context;
  }

  @Override
  public <E> PaginatorBuilder<E> from(List<E> results) {
    return new PaginatorBuilderImpl<>(results);
  }

  private class PaginatorBuilderImpl<E> implements PaginatorBuilder<E> {

    private final List<E> data;
    private int pageSize = preferences.number(UserPreference.RESULTS_PER_PAGE).get();
    private BiConsumer<Writer, E> consumer = (writer, obj) -> {
      if (obj instanceof Displayable displayable) {
        writer.writeln(displayable);
      } else {
        writer.writeln(String.valueOf(obj));
      }
    };

    private PaginatorBuilderImpl(List<E> data) {
      this.data = data;
    }

    @Override
    public PaginatorBuilder<E> pageSize(int resultsPerPage) {
      if (resultsPerPage > 0) {
        this.pageSize = resultsPerPage;
      }
      return this;
    }

    @Override
    public PaginatorBuilder<E> print(BiConsumer<Writer, E> printConsumer) {
      this.consumer = printConsumer;
      return this;
    }

    @Override
    public void paginate() {
      if (data.isEmpty()) {
        return;
      }
      int count = data.size();
      int pages = (int) Math.ceil((double) count / pageSize);

      Writer writer = context.writer();
      if (!context.isInteractive() || preferences.isDisabled(UserPreference.RESULT_PAGING) || pages == 1) {
        data.forEach(item -> consumer.accept(writer, item));
        return;
      }

      int cursor = 0;
      int end;

      boolean validInput;

      while (cursor < count) {
        end = cursor + pageSize;
        data.subList(cursor, Math.min(end, count)).forEach(item -> consumer.accept(writer, item));
        try {
          writer
            .withStyle("white").writeln("-".repeat(terminal.getWidth()))

            .withStyle("keyboard")
            .writeIcon("nf-mdi-arrow_left")
            .write(" ")
            .writeIcon("nf-mdi-arrow_up")

            .withStyle("white//b")
            .write(String.format(" (%d/%d) ", 1 + ((cursor) / pageSize), pages))

            .withStyle("keyboard")
            .writeIcon("nf-mdi-arrow_down")
            .write(" ")
            .writeIcon("nf-mdi-arrow_right")
            .write("\t")

            .withStyle("white").writeIcon("nf-fa-play").write(" ")
            .withStyle("keyboard").write("r/p ")

            .withStyle("white").writeIcon("nf-fa-stop").write(" ")
            .withStyle("keyboard").write("q/c");
          terminal.enterRawMode();

          do {
            int read = terminal.reader().read();
            validInput = true;
            switch (read) {
              // Arrows left and down
              case 67, 66 -> cursor = end;
              // Arrows right and up
              case 68, 65 -> cursor = Math.max(0, cursor - pageSize);
              case 'r', 'p' -> {
                if (end < count) {
                  data.subList(end, count).forEach(item -> consumer.accept(writer, item));
                  return;
                }
                cursor = count;
              }
              case 'q', 'c' -> {
                writer.newLine();
                return;
              }
              default -> validInput = false;
            }
          } while (!validInput);
          writer.newLine();
        } catch (IOException e) {
          throw new UnbelievableException(e);
        }
      }
    }
  }

}

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

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.CommandContext;
import com.backpackcloud.cli.Displayable;
import com.backpackcloud.cli.Writer;
import com.backpackcloud.cli.Preferences;
import com.backpackcloud.preferences.UserPreferences;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class Paginator {

  private final UserPreferences preferences;

  private final Terminal terminal;

  private final CommandContext context;

  public Paginator(UserPreferences preferences, Terminal terminal, CommandContext context) {
    this.preferences = preferences;
    this.terminal = terminal;
    this.context = context;
  }

  public <E> PaginatorBuilder<E> from(List<E> results) {
    return new PaginatorBuilder<>(results);
  }

  public <E> PaginatorBuilder<E> from(Stream<E> stream) {
    return from(stream.toList());
  }

  public class PaginatorBuilder<E> {

    private final List<E> data;
    private int pageSize = preferences.get(Preferences.RESULTS_PER_PAGE).value();
    private BiConsumer<Writer, E> consumer = (writer, obj) -> {
      if (obj instanceof Displayable displayable) {
        writer.writeln(displayable);
      } else {
        writer.writeln(String.valueOf(obj));
      }
    };

    private PaginatorBuilder(List<E> data) {
      this.data = data;
    }

    public PaginatorBuilder<E> pageSize(int resultsPerPage) {
      if (resultsPerPage > 0) {
        this.pageSize = resultsPerPage;
      }
      return this;
    }

    public PaginatorBuilder<E> print(BiConsumer<Writer, E> printConsumer) {
      this.consumer = printConsumer;
      return this;
    }

    public void paginate() {
      paginate(0);
    }

    private void paginate(int start) {
      if (data.isEmpty()) {
        return;
      }
      int count = data.size();
      int pages = (int) Math.ceil((double) count / pageSize);

      Writer writer = context.writer();
      if (preferences.isDisabled(Preferences.RESULT_PAGING) || pages == 1) {
        data.forEach(item -> consumer.accept(writer, item));
        return;
      }

      int cursor = start;
      int end;

      boolean validInput;

      while (cursor < count) {
        pageSize = Math.max(pageSize, 1);
        pages = (int) Math.ceil((double) count / pageSize);

        if (preferences.isEnabled(Preferences.CLEAR_ON_PAGING)) {
          System.out.print("\033[H\033[2J");
          System.out.flush();
        }
        end = cursor + pageSize;
        data.subList(cursor, Math.min(end, count)).forEach(item -> consumer.accept(writer, item));
        try {
          writer
            .withStyle("white").writeln("-".repeat(terminal.getWidth()))

            .withStyle("keyboard")
            .writeIcon("arrow-left")
            .write(" ")
            .writeIcon("arrow-up")

            .withStyle("white//b")
            .write(String.format(" (%d/%d) ", 1 + ((cursor) / pageSize), pages))

            .withStyle("keyboard")
            .writeIcon("arrow-down")
            .write(" ")
            .writeIcon("arrow-right")
            .write("\t")

            .withStyle("white").writeIcon("continue").write(" ")
            .withStyle("keyboard").write("r ")

            .withStyle("white").writeIcon("stop").write(" ")
            .withStyle("keyboard").write("q");
          terminal.writer().flush();
          terminal.enterRawMode();

          do {
            int read = terminal.reader().read();
            validInput = true;
            switch (read) {
              // Left Arrow
              case 67 -> cursor = end;
              // Down Arrow
              case 66 -> pageSize++;
              // Right Arrow
              case 68 -> cursor = Math.max(0, cursor - pageSize);
              // Up Arrow
              case 65 -> pageSize--;
              case 'r' -> {
                if (end < count) {
                  data.subList(end, count).forEach(item -> consumer.accept(writer, item));
                  return;
                }
                cursor = count;
              }
              case 'q' -> {
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

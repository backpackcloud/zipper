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

import com.backpackcloud.cli.CommandListener;
import com.backpackcloud.cli.CommandNotifier;
import com.backpackcloud.cli.ErrorRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DefaultCommandNotifier implements CommandNotifier {

  private final List<Runnable> startListeners = new ArrayList<>();
  private final List<Runnable> doneListeners = new ArrayList<>();
  private final List<Runnable> beforeInputListeners = new ArrayList<>();
  private final List<Consumer<Exception>> errorListeners = new ArrayList<>();
  private final ErrorRegistry errorRegistry;

  public DefaultCommandNotifier(ErrorRegistry errorRegistry) {
    this.errorRegistry = errorRegistry;
  }

  @Override
  public CommandListener listener() {
    return new CommandListener() {
      @Override
      public void beforeCommand(Runnable action) {
        startListeners.add(action);
      }

      @Override
      public void afterCommand(Runnable action) {
        doneListeners.add(action);
      }

      @Override
      public void beforeInput(Runnable action) {
        beforeInputListeners.add(action);
      }

      @Override
      public void onCommandError(Consumer<Exception> action) {
        errorListeners.add(action);
      }
    };
  }

  @Override
  public void notifyStart() {
    startListeners.forEach(Runnable::run);
  }

  @Override
  public void notifyDone() {
    doneListeners.forEach(Runnable::run);
  }

  @Override
  public void notifyReady() {
    beforeInputListeners.forEach(Runnable::run);
  }

  @Override
  public void notifyError(Exception e) {
    errorRegistry.add(e);
    errorListeners.forEach(c -> c.accept(e));
  }

}

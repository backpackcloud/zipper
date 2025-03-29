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

package com.backpackcloud.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommandBus implements CommandObserver, CommandNotifier {

  private final List<Runnable> startListeners = new ArrayList<>();
  private final List<Runnable> doneListeners = new ArrayList<>();
  private final List<Runnable> beforeInputListeners = new ArrayList<>();
  private final List<Consumer<Exception>> errorListeners = new ArrayList<>();

  private final ErrorRegistry errorRegistry;

  public CommandBus(ErrorRegistry errorRegistry) {
    this.errorRegistry = errorRegistry;
  }

  public void beforeCommand(Runnable action) {
    startListeners.add(action);
  }

  public void afterCommand(Runnable action) {
    doneListeners.add(action);
  }

  public void beforeInput(Runnable action) {
    beforeInputListeners.add(action);
  }

  public void onCommandError(Consumer<Exception> action) {
    errorListeners.add(action);
  }

  public void notifyStart() {
    startListeners.forEach(Runnable::run);
  }

  public void notifyDone() {
    doneListeners.forEach(Runnable::run);
  }

  public void notifyReady() {
    beforeInputListeners.forEach(Runnable::run);
  }

  public void notifyError(Exception e) {
    errorRegistry.add(e);
    errorListeners.forEach(c -> c.accept(e));
  }

}

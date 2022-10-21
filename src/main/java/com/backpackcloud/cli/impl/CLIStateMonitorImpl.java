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

import com.backpackcloud.cli.CLIStateMonitor;
import com.backpackcloud.cli.CommandListener;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class CLIStateMonitorImpl implements CLIStateMonitor {

  private final AtomicBoolean running = new AtomicBoolean();

  private final AtomicBoolean started = new AtomicBoolean();

  private final List<Runnable> listeners = new ArrayList<>();


  public CLIStateMonitorImpl(CommandListener listener) {
    listener.beforeInput(() -> {
      if (this.started.compareAndSet(false, true)) {
        this.listeners.forEach(Runnable::run);
      }
    });
    listener.beforeCommand(() -> running.set(true));
    listener.afterCommand(() -> running.set(false));
  }


  @Override
  public void onceStarted(Runnable action) {
    this.listeners.add(action);
  }

  @Override
  public boolean isStarted() {
    return started.get();
  }

  @Override
  public boolean isCommandRunning() {
    return running.get();
  }

}

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

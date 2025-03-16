package com.backpackcloud.cli;

public interface CommandNotifier {

  void notifyStart();

  void notifyDone();

  void notifyReady();

  void notifyError(Exception e);

}

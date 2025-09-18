package com.backpackcloud.cli;

import com.backpackcloud.cli.builder.CLIBuilder;

public interface Module {

  void configure(CLIBuilder builder);

}

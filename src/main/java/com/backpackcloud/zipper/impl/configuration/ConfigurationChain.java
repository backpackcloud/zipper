package com.backpackcloud.zipper.impl.configuration;

import com.backpackcloud.zipper.Configuration;

import java.util.List;
import java.util.function.Supplier;

public class ConfigurationChain implements Configuration {

  private final Configuration configuration;

  public ConfigurationChain(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public boolean isSet() {
    return configuration.isSet();
  }

  @Override
  public String get() {
    return configuration.get();
  }

  @Override
  public int asInt() {
    return configuration.asInt();
  }

  @Override
  public long asLong() {
    return configuration.asLong();
  }

  @Override
  public boolean asBoolean() {
    return configuration.asBoolean();
  }

  @Override
  public String read() {
    return configuration.read();
  }

  @Override
  public List<String> readLines() {
    return configuration.readLines();
  }

  @Override
  public Configuration or(Configuration defaultConfiguration) {
    return configuration.or(defaultConfiguration);
  }

  @Override
  public String or(Supplier<String> supplier) {
    return configuration.or(supplier);
  }

  @Override
  public String orElse(String defaultValue) {
    return configuration.orElse(defaultValue);
  }

  @Override
  public int orElse(int defaultValue) {
    return configuration.orElse(defaultValue);
  }

  @Override
  public long orElse(long defaultValue) {
    return configuration.orElse(defaultValue);
  }

  @Override
  public boolean orElse(boolean defaultValue) {
    return configuration.orElse(defaultValue);
  }

  public ConfigurationChain env(String key) {
    return new ConfigurationChain(configuration.or(new EnvironmentVariableConfiguration(key)));
  }

  public ConfigurationChain file(String key) {
    return new ConfigurationChain(configuration.or(new FileConfiguration(key)));
  }

  public ConfigurationChain resource(String key) {
    return new ConfigurationChain(configuration.or(new ResourceConfiguration(key)));
  }

  public ConfigurationChain property(String key) {
    return new ConfigurationChain(configuration.or(new SystemPropertyConfiguration(key)));
  }

  public ConfigurationChain url(String key) {
    return new ConfigurationChain(configuration.or(new UrlConfiguration(key)));
  }

  public ConfigurationChain value(String key) {
    return new ConfigurationChain(configuration.or(new RawValueConfiguration(key)));
  }

}

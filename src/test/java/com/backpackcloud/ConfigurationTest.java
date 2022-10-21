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

package com.backpackcloud;

import com.backpackcloud.configuration.Configuration;
import com.backpackcloud.configuration.EnvironmentVariableConfiguration;
import com.backpackcloud.configuration.FileConfiguration;
import com.backpackcloud.configuration.NotSuppliedConfiguration;
import com.backpackcloud.configuration.RawValueConfiguration;
import com.backpackcloud.configuration.ResourceConfiguration;
import com.backpackcloud.configuration.SystemPropertyConfiguration;
import com.backpackcloud.configuration.ConfigurationDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationTest {

  ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
  TestObject   testObject;


  public ConfigurationTest() {
    objectMapper.registerModule(new SimpleModule().addDeserializer(Configuration.class, new ConfigurationDeserializer()));
    init();
  }

  public void init() {
    try {
      testObject = objectMapper.readValue(
          new FileInputStream("src/test/resources/com/backpackcloud/impl/configuration.yaml"),
          TestObject.class
      );
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  @AfterEach
  public void reset() {
    System.clearProperty("fakeomatic.test");
  }

  private Configuration value(String key) {
    return testObject.map.get(key);
  }

  @Test
  public void testRawValue() {
    Configuration value = value("raw_value");
    assertTrue(value instanceof RawValueConfiguration);
    assertTrue(value.isSet());
    assertEquals("foo", value.get());

    value = value("raw_value2");
    assertTrue(value instanceof RawValueConfiguration);
    assertTrue(value.isSet());
    assertEquals("bar", value.get());

    value = value("raw_composite");
    assertTrue(value instanceof RawValueConfiguration);
    assertTrue(value.isSet());
    assertEquals("foo", value.get());
  }

  //@Test TODO adjust so it doesn't fail on Windows
  public void testEnvironmentVariableValue() {
    Configuration value = value("env_value");
    assertTrue(value instanceof EnvironmentVariableConfiguration);
    assertTrue(value.isSet());
    assertEquals(System.getenv("PATH"), value.get());

    value = value("env_composite");
    assertTrue(value instanceof EnvironmentVariableConfiguration);
    assertTrue(value.isSet());
    assertEquals(System.getenv("PATH"), value.get());
  }

  @Test
  public void testSystemPropertyValue() {
    Configuration value = value("property_value");
    assertFalse(value.isSet());

    System.setProperty("fakeomatic.test", "bar");

    assertFalse(value instanceof SystemPropertyConfiguration);
    assertTrue(value instanceof NotSuppliedConfiguration);
    assertFalse(value.isSet());

    init();

    value = value("property_value");
    assertTrue(value instanceof SystemPropertyConfiguration);
    assertTrue(value.isSet());
    assertEquals("bar", value.get());
  }

  @Test
  public void testFileValue() {
    Configuration value = value("file_value");
    assertTrue(value instanceof FileConfiguration);
    assertTrue(value.isSet());
    assertTrue(value.get().contains("The MIT License (MIT)"));

    assertFalse(value("file_value2").isSet());

    value = value("file_composite");
    assertTrue(value instanceof FileConfiguration);
    assertTrue(value.isSet());
    assertTrue(value.get().contains("The MIT License (MIT)"));
  }

  @Test
  public void testResourceValue() {
    Configuration value = value("resource_value");
    assertTrue(value instanceof ResourceConfiguration);
    assertTrue(value.isSet());
    assertTrue(value.get().contains("raw_value: foo"));
    assertTrue(value.get().contains("raw_value: foo"));

    value = value("resource_composite");
    assertTrue(value instanceof ResourceConfiguration);
    assertTrue(value.isSet());
    assertTrue(value.get().contains("raw_value: foo"));
    assertTrue(value.get().contains("raw_value: foo"));
  }

  @Test
  public void testIntConversion() {
    Configuration value = value("int_value");
    assertTrue(value.isSet());
    assertEquals("10", value.get());
    assertEquals(10, value.asInt());
  }

  @Test
  public void testLongConversion() {
    Configuration value = value("long_value");
    assertTrue(value.isSet());
    assertEquals("100000000000000", value.get());
    assertEquals(100000000000000L, value.asLong());
  }

  @Test
  public void testBooleanConversion() {
    Configuration value = value("boolean_value");
    assertTrue(value.isSet());
    assertEquals("true", value.get());
    assertTrue(value.asBoolean());
  }

  @Test
  public void testNotSuppliedConfiguration() {
    Configuration value = value("not_defined");
    assertTrue(value instanceof NotSuppliedConfiguration);
    assertFalse(value.isSet());
    assertEquals("", value.get());
    assertEquals(0, value.asInt());
    assertEquals(0L, value.asLong());
    assertFalse(value.asBoolean());
    assertEquals("", value.read());
    assertTrue(value.readLines().isEmpty());
  }

  public static class TestObject {

    public final Map<String, Configuration> map;

    @JsonCreator
    public TestObject(Map<String, Configuration> map) {
      this.map = map;
    }

  }

}

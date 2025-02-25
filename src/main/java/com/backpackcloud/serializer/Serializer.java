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

package com.backpackcloud.serializer;

import com.backpackcloud.configuration.Configuration;
import com.backpackcloud.text.InputValue;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.File;
import java.io.InputStream;

/**
 * Interface that exposes components for serializing different formats of input.
 */
public interface Serializer {

  /**
   * Serializes the given object into a String.
   *
   * @param object the object to serialize
   * @return the serialized object.
   */
  String serialize(Object object);

  /**
   * Deserialize the given input into an object of the given class.
   *
   * @param input the input to deserialize
   * @param type  the type of the result object
   * @return the deserialized object.
   */
  <E> E deserialize(String input, Class<E> type);

  /**
   * Deserialize the given file content into an object of the given class.
   *
   * @param file the file containing the input to deserialize
   * @param type the type of the result object
   * @return the deserialized object.
   */
  <E> E deserialize(File file, Class<E> type);

  /**
   * Deserialize the given input into an object of the given class.
   *
   * @param input the input to deserialize
   * @param type  the type of the result object
   * @return the deserialized object.
   */
  <E> E deserialize(InputStream input, Class<E> type);

  default <E> E deserialize(Configuration configuration, Class<E> type) {
    return deserialize(configuration.read(), type);
  }

  Serializer addDependency(String name, Object dependency);

  <E> Serializer addDependency(Class<E> type, E dependency);

  /**
   * Returns the mapper for json data type.
   *
   * @return the mapper for json data type.
   */
  static Serializer json() {
    ObjectMapper jsonMapper = new ObjectMapper();
    configure(jsonMapper);
    return new JacksonSerializer(jsonMapper);
  }

  /**
   * Returns the mapper for the yaml data type.
   *
   * @return the mapper for the yaml data type.
   */
  static Serializer yaml() {
    ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    configure(yamlMapper);
    return new JacksonSerializer(yamlMapper);
  }

  private static void configure(ObjectMapper mapper) {
    SimpleModule backpackModule = new SimpleModule();
    backpackModule.addDeserializer(Configuration.class, new ConfigurationDeserializer());
    backpackModule.addDeserializer(InputValue.class, new InputValueDeserializer());

    mapper.registerModules(new Jdk8Module(), new JavaTimeModule(), new ParameterNamesModule(), backpackModule);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

}

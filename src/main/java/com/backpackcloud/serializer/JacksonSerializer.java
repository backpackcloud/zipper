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

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.configuration.Configuration;
import com.backpackcloud.configuration.ConfigurationDeserializer;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

public class JacksonSerializer implements Serializer {

  private final ObjectMapper objectMapper;
  private final InjectableValues.Std values;

  public JacksonSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;

    this.values = new InjectableValues.Std();
    this.objectMapper.setInjectableValues(values);

    SimpleModule simpleModule = new SimpleModule();
    simpleModule.addDeserializer(Configuration.class, new ConfigurationDeserializer());
    this.objectMapper.registerModule(simpleModule);

    addDependency(Serializer.class, this);
  }

  @Override
  public Serializer addDependency(String name, Object dependency) {
    values.addValue(name, dependency);
    return this;
  }

  @Override
  public <E> Serializer addDependency(Class<E> type, E dependency) {
    values.addValue(type, dependency);
    return this;
  }

  @Override
  public ObjectMapper mapper() {
    return objectMapper;
  }

  @Override
  public String serialize(Object object) {
    Writer writer = new StringWriter();
    try {
      objectMapper.writeValue(writer, object);
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
    return writer.toString();
  }

  @Override
  public <E> E deserialize(String input, Class<E> type) {
    try {
      return objectMapper.readValue(input, type);
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  @Override
  public <E> E deserialize(File file, Class<E> type) {
    try {
      return objectMapper.readValue(file, type);
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  @Override
  public <E> E deserialize(InputStream input, Class<E> type) {
    try (input) {
      return objectMapper.readValue(input, type);
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }


}

package com.backpackcloud.zipper.impl.serializer;

import com.backpackcloud.zipper.Serializer;
import com.backpackcloud.zipper.UnbelievableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class SerializerImpl implements Serializer {

  private final ObjectMapper objectMapper;
  private final InjectableValues.Std values;

  public SerializerImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.values = new InjectableValues.Std();
    this.objectMapper.setInjectableValues(values);
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

}

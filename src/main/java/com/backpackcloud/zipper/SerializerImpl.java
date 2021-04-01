package com.backpackcloud.zipper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class SerializerImpl implements Serializer {

  private final ObjectMapper jsonMapper;
  private final ObjectMapper yamlMapper;

  private final InjectableValues.Std values;

  public SerializerImpl() {
    values = new InjectableValues.Std();

    jsonMapper = new ObjectMapper();
    jsonMapper.registerModules(new Jdk8Module(), new JavaTimeModule(), new ParameterNamesModule());
    jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    jsonMapper.setInjectableValues(values);

    yamlMapper = new ObjectMapper(new YAMLFactory());
    yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    yamlMapper.registerModules(new Jdk8Module(), new JavaTimeModule());
    yamlMapper.setInjectableValues(values);
  }

  public SerializerImpl(ObjectMapper jsonMapper, ObjectMapper yamlMapper) {
    values = new InjectableValues.Std();

    this.jsonMapper = jsonMapper;
    this.yamlMapper = yamlMapper;

    this.jsonMapper.setInjectableValues(values);
    this.yamlMapper.setInjectableValues(values);
  }

  @Override
  public Mapper json() {
    return new MapperImpl(jsonMapper);
  }

  @Override
  public Mapper yaml() {
    return new MapperImpl(yamlMapper);
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

  static class MapperImpl implements Mapper {

    private final ObjectMapper objectMapper;

    MapperImpl(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
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
    public TreeNode deserialize(String input) {
      try {
        return objectMapper.readTree(input);
      } catch (JsonProcessingException e) {
        throw new UnbelievableException(e);
      }
    }

    @Override
    public ObjectMapper getDelegate() {
      return objectMapper;
    }
  }

}

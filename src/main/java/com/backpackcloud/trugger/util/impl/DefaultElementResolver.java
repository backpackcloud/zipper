package com.backpackcloud.trugger.util.impl;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.trugger.reflection.ReflectedField;
import com.backpackcloud.trugger.reflection.ReflectedMethod;
import com.backpackcloud.trugger.reflection.Reflection;
import com.backpackcloud.trugger.util.ElementResolver;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DefaultElementResolver implements ElementResolver {

  private final Object target;

  public DefaultElementResolver(Object target) {
    this.target = target;
  }

  @Override
  public <E> E resolve(String name) {
    Iterator<String> iterator = List.of(name.split("\\.")).iterator();
    Object result = target;
    while (iterator.hasNext()) {
      result = resolve(iterator.next(), result);
    }
    return (E) result;
  }

  private Object resolve(String name, Object target) {
    if (target instanceof Map map) {
      return map.get(name);
    } else if (target instanceof JsonNode json) {
      return json.get(name);
    }
    return Reflection.reflect().method(name)
      .withoutParameters()
      .from(target)
      .map(ReflectedMethod::invoke)
      .or(() -> Reflection.reflect()
        .field(name)
        .from(target)
        .map(ReflectedField::get))
      .orElseThrow(UnbelievableException::new);
  }

}

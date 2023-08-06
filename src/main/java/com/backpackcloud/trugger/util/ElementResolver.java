package com.backpackcloud.trugger.util;

import com.backpackcloud.trugger.util.impl.DefaultElementResolver;

public interface ElementResolver {

  <E> E resolve(String name);

  default <E> E resolve(String name, Class<E> type) {
    Object value = resolve(name);
    return type.cast(value);
  }

  static ElementResolver of(Object target) {
    return new DefaultElementResolver(target);
  }

}

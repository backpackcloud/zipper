package com.backpackcloud.serializer.cdi;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.serializer.JSON;
import com.backpackcloud.serializer.Serializer;
import com.backpackcloud.serializer.YAML;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class Producers {

  private Serializer yamlSerializer;
  private Serializer jsonSerializer;

  @Produces
  public Serializer produceSerializer(InjectionPoint point) {
    if (point.getAnnotated().isAnnotationPresent(YAML.class)) {
      if (yamlSerializer == null) {
        yamlSerializer = Serializer.yaml();
      }
      return yamlSerializer;
    } else if (point.getAnnotated().isAnnotationPresent(JSON.class)) {
      if (jsonSerializer == null) {
        jsonSerializer = Serializer.json();
      }
      return jsonSerializer;
    }
    throw new UnbelievableException("No @YAML or @JSON annotation found");
  }

}

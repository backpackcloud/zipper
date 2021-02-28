package com.backpackcloud.zipper.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.backpackcloud.trugger.element.ElementPredicates.writable;
import static com.backpackcloud.trugger.element.Elements.elements;

public class PropertyFilter {

  private Set<String> fields;

  @JsonCreator
  public PropertyFilter(String fields) {
    if (fields != null && !fields.isBlank()) {
      this.fields = new HashSet<>(Arrays.asList(fields.split(",")));
    } else {
      this.fields = Collections.emptySet();
    }
  }

  public void apply(ApiResourceModel model) {
    if (fields.isEmpty()) return;

    elements().from(model).stream()
      .filter(writable())
      .filter(element -> {
        String name = element.name();
        if (element.isAnnotationPresent(JsonProperty.class)) {
          String mappedName = element.getAnnotation(JsonProperty.class).value();
          if (!mappedName.equals(JsonProperty.USE_DEFAULT_NAME)) {
            name = mappedName;
          }
        }
        return !fields.contains(name);
      })
      .forEach(element -> element.setValue(null));
  }

  public static PropertyFilter NONE = new PropertyFilter(null);

}

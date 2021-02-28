package com.backpackcloud.zipper.api;

import com.backpackcloud.zipper.domain.Entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.backpackcloud.trugger.element.ElementPredicates.annotatedWith;
import static com.backpackcloud.trugger.element.Elements.elements;

public class EntityModel<E extends Entity> {

  @JsonUnwrapped
  private final E result;
  @JsonProperty("_links")
  private final Map<String, EntityLink> links;

  public EntityModel(E result) {
    this.result = result;
    this.links = new HashMap<>();
    initializeLinks();
  }

  private void initializeLinks() {
    links.put("_self", new EntityLink(result));
    elements()
      .filter(annotatedWith(Link.class))
      .from(result)
      .forEach(element -> {
        Link link = element.getAnnotation(Link.class);
        String rel = link.rel().isEmpty() ? element.name() : link.rel();
        String title = link.title().isEmpty() ? element.type().getSimpleName() : link.title();
        this.links.put(rel, new EntityLink(element.getValue(), title));
      });
  }

  public E result() {
    return result;
  }

  public Map<String, EntityLink> links() {
    return Collections.unmodifiableMap(links);
  }

}

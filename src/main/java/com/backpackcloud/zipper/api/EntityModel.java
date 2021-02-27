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
  private final String baseUrl;
  @JsonProperty("_links")
  private final Map<String, EntityLink> links;

  public EntityModel(E result, String baseUrl) {
    this.result = result;
    this.baseUrl = baseUrl;
    this.links = new HashMap<>();
    initializeLinks();
  }

  private void initializeLinks() {
    elements()
      .filter(annotatedWith(Link.class))
      .from(result)
      .forEach(element -> {
        Link link = element.getAnnotation(Link.class);
        String rel = link.rel().isEmpty() ? element.name() : link.rel();
        Entity entity = element.getValue();
        ApiResource resource = entity.getClass().getAnnotation(ApiResource.class);
        String href = String.format("%s/%s/%s", baseUrl, resource.name(), entity.id());
        this.links.put(rel, new EntityLink(href, link.title()));
      });
  }

  public E result() {
    return result;
  }

  public Map<String, EntityLink> links() {
    return Collections.unmodifiableMap(links);
  }

}

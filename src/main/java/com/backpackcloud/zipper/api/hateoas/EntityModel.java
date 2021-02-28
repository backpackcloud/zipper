package com.backpackcloud.zipper.api.hateoas;

import com.backpackcloud.trugger.reflection.ReflectedConstructor;
import com.backpackcloud.zipper.UnbelievableException;
import com.backpackcloud.zipper.api.ApiResource;
import com.backpackcloud.zipper.api.ApiResourceModel;
import com.backpackcloud.zipper.api.PropertyFilter;
import com.backpackcloud.zipper.domain.Entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.backpackcloud.trugger.element.ElementPredicates.annotatedWith;
import static com.backpackcloud.trugger.element.Elements.copy;
import static com.backpackcloud.trugger.element.Elements.elements;
import static com.backpackcloud.trugger.reflection.Reflection.reflect;

public class EntityModel<E extends Entity> {

  @JsonUnwrapped
  private final ApiResourceModel resourceModel;
  private ApiResource annotation;
  @JsonProperty("_links")
  private final Map<String, ApiLink> links;

  public EntityModel(E result) {
    this(result, PropertyFilter.NONE);
  }

  public EntityModel(E result, PropertyFilter filter) {
    this.annotation = result.getClass().getAnnotation(ApiResource.class);
    this.links = new HashMap<>();
    Optional<ReflectedConstructor> constructor = reflect()
      .constructor()
      .withParameters(result.getClass())
      .from(annotation.model());
    if (constructor.isPresent()) {
      this.resourceModel = constructor.get().invoke(result);
    } else {
      this.resourceModel = reflect()
        .constructor()
        .withoutParameters()
        .from(annotation.model())
        .orElseThrow(UnbelievableException::new)
        .invoke();
      copy().from(result).to(this.resourceModel);
    }
    initializeLinks();
    filter.apply(resourceModel);
  }

  private void initializeLinks() {
    links.put("_self", new EntityLink("_self", resourceModel));
    elements()
      .filter(annotatedWith(Link.class))
      .from(resourceModel)
      .forEach(element -> {
        Link link = element.getAnnotation(Link.class);
        String rel = link.rel().isEmpty() ? element.name() : link.rel();
        String title = link.title().isEmpty() ? element.type().getSimpleName() : link.title();
        this.links.put(rel, new EntityLink(rel, element.getValue(), title));
      });
  }

}

package com.backpackcloud.zipper.api.hateoas;

import com.backpackcloud.trugger.reflection.ReflectedConstructor;
import com.backpackcloud.zipper.UnbelievableException;
import com.backpackcloud.zipper.api.ApiResource;
import com.backpackcloud.zipper.api.ApiResourceModel;
import com.backpackcloud.zipper.domain.Entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.*;

import static com.backpackcloud.trugger.element.ElementPredicates.annotatedWith;
import static com.backpackcloud.trugger.element.ElementPredicates.writable;
import static com.backpackcloud.trugger.element.Elements.copy;
import static com.backpackcloud.trugger.element.Elements.elements;
import static com.backpackcloud.trugger.reflection.Reflection.reflect;

public class EntityModel<E extends Entity> {

  @JsonUnwrapped
  private final ApiResourceModel resourceModel;
  private final E result;
  private ApiResource annotation;
  @JsonProperty("_links")
  private final Map<String, EntityLink> links;
  private final Set<String> fields;

  public EntityModel(E result) {
    this(result, Collections.emptySet());
  }

  public EntityModel(E result, Set<String> fields) {
    this.result = result;
    this.annotation = result.getClass().getAnnotation(ApiResource.class);
    this.links = new HashMap<>();
    this.fields = fields.isEmpty() ? Collections.emptySet() : new HashSet<>(fields);
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
    applyFilter();
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

  private void applyFilter() {
    if (fields.isEmpty()) return;

    elements().from(result).stream()
      .filter(writable())
      .filter(element -> {
        String name = element.name();
        if (element.isAnnotationPresent(JsonProperty.class)) {
          String mappedName = element.getAnnotation(JsonProperty.class).value();
          if (!mappedName.equals(JsonProperty.USE_DEFAULT_NAME)) {
            name = mappedName;
          }
        }
        return fields.contains(name);
      })
      .forEach(element -> element.setValue(null));
  }

  public E result() {
    return result;
  }

  public Map<String, EntityLink> links() {
    return Collections.unmodifiableMap(links);
  }

}

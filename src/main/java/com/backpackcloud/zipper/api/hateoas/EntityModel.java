package com.backpackcloud.zipper.api.hateoas;

import com.backpackcloud.trugger.element.Element;
import com.backpackcloud.trugger.element.Elements;
import com.backpackcloud.zipper.UnbelievableException;
import com.backpackcloud.zipper.domain.Entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityModel<E extends Entity> implements ApiModel<E> {

  private static final Pattern PATH_PATTERN = Pattern.compile("\\{(?<name>\\w+)}");

  @JsonUnwrapped
  private final E entity;
  @JsonProperty("_links")
  private final Map<String, ApiLink> links = new HashMap<>();

  public EntityModel(E entity) {
    this.entity = entity;
    initializeLinks();
  }

  private void initializeLinks() {
    if (entity.getClass().isAnnotationPresent(Link.class)) {
      Link[] declaredLinks = entity.getClass().getAnnotationsByType(Link.class);
      for (Link link : declaredLinks) {
        StringBuilder uri = new StringBuilder(link.uri());
        Matcher matcher = PATH_PATTERN.matcher(uri);
        while (matcher.find()) {
          String value = Elements.element(matcher.group("name"))
              .from(entity)
              .map(Element::getValue)
              .map(Object::toString)
              .orElseThrow(UnbelievableException::new);
          uri.replace(matcher.start(), matcher.end(), value);
        }
        LinkMapper<ApiModel<E>> mapper = link(uri.toString());
        if (!link.title().isBlank()) mapper.title(link.title());
        mapper.to(link.rel());
      }
    }
  }

  @Override
  public E entity() {
    return entity;
  }

  @Override
  public LinkMapper<ApiModel<E>> link(String uri) {
    return new LinkMapper<>() {
      String title;

      @Override
      public LinkMapper<ApiModel<E>> title(String title) {
        this.title = title;
        return this;
      }

      @Override
      public ApiModel<E> to(String rel) {
        links.put(rel, new EntityLink(uri, rel, title));
        return EntityModel.this;
      }
    };
  }

  @Override
  public Optional<ApiLink> linkTo(String rel) {
    return Optional.ofNullable(links.get(rel));
  }

}

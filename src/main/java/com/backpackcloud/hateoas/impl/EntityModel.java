package com.backpackcloud.hateoas.impl;

import com.backpackcloud.hateoas.ApiLink;
import com.backpackcloud.hateoas.ApiModel;
import com.backpackcloud.hateoas.Link;
import com.backpackcloud.hateoas.LinkMapper;
import com.backpackcloud.text.Interpolator;
import com.backpackcloud.trugger.reflection.Reflection;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class EntityModel<E> implements ApiModel<E> {

  @JsonUnwrapped
  private final E entity;
  @JsonProperty("_links")
  private final Map<String, ApiLink> links = new HashMap<>();

  public EntityModel(E entity) {
    this.entity = entity;
    initializeLinks();
  }

  private void initializeLinks() {
    Link[] declaredLinks = entity.getClass().getAnnotationsByType(Link.class);
    if (declaredLinks.length == 0) return;

    Interpolator interpolator = new Interpolator(Reflection.elementResolver(entity));
    for (Link link : declaredLinks) {
      interpolator.eval(link.uri())
        .ifPresent(uri -> interpolator.eval(link.title())
          .ifPresent(title -> {
            LinkMapper<ApiModel<E>> mapper = link(uri);
            mapper.title(title);
            mapper.to(link.rel());
          }));
    }
  }

  @Override
  public E data() {
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

package com.backpackcloud.zipper.api.hateoas;

import com.backpackcloud.zipper.domain.Entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.HashMap;
import java.util.Map;

public class EntityModel<E extends Entity> implements ApiModel<E> {

  @JsonUnwrapped
  private final E entity;
  @JsonProperty("_links")
  private final Map<String, ApiLink> links = new HashMap<>();

  public EntityModel(E entity) {
    this.entity = entity;
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

}

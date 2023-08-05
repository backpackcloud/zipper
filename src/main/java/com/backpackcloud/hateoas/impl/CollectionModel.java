package com.backpackcloud.hateoas.impl;

import com.backpackcloud.hateoas.ApiCollectionModel;
import com.backpackcloud.hateoas.ApiLink;
import com.backpackcloud.hateoas.ApiModel;
import com.backpackcloud.hateoas.LinkMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CollectionModel<E> implements ApiCollectionModel<E> {

  @JsonProperty("values")
  private final Collection<ApiModel<E>> values;
  @JsonProperty("_links")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private final Map<String, ApiLink> links;
  @JsonProperty("total")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Long total;

  public CollectionModel(Collection<E> values, Long total) {
    this.values = values.stream()
      .map(EntityModel::new)
      .collect(Collectors.toList());
    this.links = new HashMap<>();
    this.total = total;
  }

  public CollectionModel(Collection<E> values) {
    this(values, null);
  }

  @Override
  public LinkMapper<ApiCollectionModel<E>> link(String uri) {
    return new LinkMapper<>() {
      String title;

      @Override
      public LinkMapper<ApiCollectionModel<E>> title(String title) {
        this.title = title;
        return this;
      }

      @Override
      public CollectionModel<E> to(String rel) {
        links.put(rel, new EntityLink(uri, rel, title));
        return CollectionModel.this;
      }
    };
  }

  @JsonProperty("size")
  public int size() {
    return values.size();
  }

  @Override
  public Collection<ApiModel<E>> values() {
    return values;
  }

  @Override
  public Optional<ApiLink> linkTo(String rel) {
    return Optional.ofNullable(links.get(rel));
  }

}

package com.backpackcloud.zipper.api.hateoas;

import com.backpackcloud.zipper.domain.Entity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionModel<E extends Entity> {

  private final List<EntityModel<E>> list;
  private final Map<String, ApiLink> links;
  private final Long total;

  public CollectionModel(Collection<E> list) {
    this(list, null);
  }

  public CollectionModel(Collection<E> list, Long total) {
    this.list = list.stream()
      .map(EntityModel::new)
      .collect(Collectors.toList());
    this.total = total;
    this.links = new HashMap<>();
  }

  public LinkMapper<CollectionModel<E>> link(String uriFormat, Object... args) {
    return link(String.format(uriFormat, args));
  }

  public LinkMapper<CollectionModel<E>> link(String uri) {
    return new LinkMapper<>() {
      String title;

      @Override
      public LinkMapper<CollectionModel<E>> title(String title) {
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

  @JsonProperty("count")
  public int count() {
    return list.size();
  }

  @JsonProperty("total")
  public Long total() {
    return total;
  }

  @JsonProperty("list")
  public List<EntityModel<E>> list() {
    return list;
  }

  @JsonProperty("_links")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public Map<String, ApiLink> links() {
    return links;
  }

}

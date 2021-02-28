package com.backpackcloud.zipper.api.hateoas;

import com.backpackcloud.zipper.domain.Entity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionModel<E extends Entity> {

  private final List<EntityModel<E>> result;
  private final Map<String, EntityLink> links;

  public CollectionModel(List<E> result) {
    this.result = result.stream()
      .map(EntityModel::new)
      .collect(Collectors.toList());
    this.links = new HashMap<>();
  }

  @JsonProperty("list")
  public List<EntityModel<E>> result() {
    return result;
  }

  @JsonProperty("total")
  public int total() {
    return result.size();
  }

  @JsonProperty("_links")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public Map<String, EntityLink> links() {
    return links;
  }

}

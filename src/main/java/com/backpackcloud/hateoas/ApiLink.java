package com.backpackcloud.hateoas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public interface ApiLink {

  @JsonProperty
  String href();

  String rel();

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_NULL)
  String title();

  @JsonIgnore
  default URI uri() {
    return URI.create(href());
  }

}

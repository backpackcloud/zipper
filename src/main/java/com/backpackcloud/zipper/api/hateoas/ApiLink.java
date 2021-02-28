package com.backpackcloud.zipper.api.hateoas;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface ApiLink {

  @JsonProperty
  String href();

  String rel();

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_NULL)
  String title();

}

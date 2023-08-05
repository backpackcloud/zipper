package com.backpackcloud.hateoas.impl;

import com.backpackcloud.hateoas.ApiLink;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.backpackcloud.configuration.Configuration.configuration;

public class EntityLink implements ApiLink {

  private static final String API_BASE_URL = configuration()
    .env("API_BASE_URL")
    .property("api.base.url")
    .or(() -> "http://localhost:8080");

  private final String rel;
  private final String href;
  private final String title;

  public EntityLink(String uri, String rel, String title) {
    this.rel = rel;
    this.href = API_BASE_URL + uri;
    this.title = title;
  }

  @JsonProperty
  @Override
  public String title() {
    return title;
  }

  @JsonProperty
  @Override
  public String href() {
    return href;
  }

  @Override
  public String rel() {
    return rel;
  }

}

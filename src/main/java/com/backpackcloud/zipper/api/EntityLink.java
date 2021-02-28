package com.backpackcloud.zipper.api;

import com.backpackcloud.zipper.domain.Entity;
import com.backpackcloud.zipper.impl.configuration.EnvironmentVariableConfiguration;
import com.backpackcloud.zipper.impl.configuration.SystemPropertyConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityLink {

  private static final String API_BASE_URL = new EnvironmentVariableConfiguration("API_BASE_URL")
    .or(new SystemPropertyConfiguration("api.base.url"))
    .or(() -> String.format("http://%s:%s",
      System.getProperty("quarkus.http.host", "localhost"),
      System.getProperty("quarkus.http.port", "8080")));

  private final String href;
  private final String title;

  public EntityLink(Entity entity, String title) {
    ApiResource resource = entity.getClass().getAnnotation(ApiResource.class);
    String href = String.format("/%s/%s", resource.name(), entity.id());

    this.href = API_BASE_URL + href;
    this.title = title;
  }

  public EntityLink(Entity entity) {
    this(entity, null);
  }

  @JsonProperty
  public String title() {
    return title;
  }

  @JsonProperty
  public String href() {
    return href;
  }

}

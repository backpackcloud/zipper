package com.backpackcloud.zipper.api.hateoas;

import com.backpackcloud.zipper.api.ApiResourceModel;
import com.backpackcloud.zipper.impl.configuration.EnvironmentVariableConfiguration;
import com.backpackcloud.zipper.impl.configuration.SystemPropertyConfiguration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.Path;

public class EntityLink {

  private static final String API_BASE_URL = new EnvironmentVariableConfiguration("API_BASE_URL")
    .or(new SystemPropertyConfiguration("api.base.url"))
    .or(() -> String.format("http://%s:%s",
      System.getProperty("quarkus.http.host", "localhost"),
      System.getProperty("quarkus.http.port", "8080")));

  private final String href;
  private final String title;

  public EntityLink(ApiResourceModel model, String title) {
    Path path = model.controllerClass().getAnnotation(Path.class);

    this.href = API_BASE_URL + path.value() + "/" + model.id();
    this.title = title;
  }

  public EntityLink(ApiResourceModel model) {
    this(model, null);
  }

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String title() {
    return title;
  }

  @JsonProperty
  public String href() {
    return href;
  }

}

package com.backpackcloud.zipper.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityLink {

  private final String href;
  private final String title;

  public EntityLink(String href, String title) {
    this.href = href != null && href.isEmpty() ? null : href;
    this.title = title;
  }

  public EntityLink(String href) {
    this.href = href;
    this.title = null;
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

package com.backpackcloud.zipper.api;

public class CollectionFilter {

  private final int maxResults;
  private final int page;

  public CollectionFilter(int maxResults, int page) {
    this.maxResults = maxResults;
    this.page = page;
  }

  public int maxResults() {
    return maxResults;
  }

  public int page() {
    return page;
  }

}

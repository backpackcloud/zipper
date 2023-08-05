package com.backpackcloud.classification.impl;

import com.backpackcloud.classification.Tags;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class DefaultTagSet implements Tags {

  private final Set<String> tags;

  public DefaultTagSet(Set<String> tags) {
    this.tags = tags;
  }

  @Override
  public boolean contains(String tagName) {
    return tags.contains(tagName);
  }

  @Override
  public Tags add(String tagName) {
    tags.add(tagName);
    return this;
  }

  @Override
  public Tags remove(String tag) {
    tags.remove(tag);
    return this;
  }

  @Override
  public Set<String> all() {
    return new HashSet<>(tags);
  }

  @Override
  public Stream<String> stream() {
    return tags.stream();
  }

  @Override
  public int size() {
    return tags.size();
  }

  @Override
  public boolean isEmpty() {
    return tags.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DefaultTagSet that = (DefaultTagSet) o;
    return Objects.equals(tags, that.tags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tags);
  }

  @Override
  public String toString() {
    return String.join(", ", tags);
  }

}

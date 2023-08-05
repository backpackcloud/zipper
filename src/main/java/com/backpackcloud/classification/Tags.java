package com.backpackcloud.classification;

import com.backpackcloud.classification.impl.DefaultTagSet;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RegisterForReflection
public interface Tags {

  boolean contains(String tag);

  default boolean contains(Tags tags) {
    return tags.stream().allMatch(this::contains);
  }

  Tags add(String tag);

  Tags remove(String tag);

  @JsonValue
  Set<String> all();

  Stream<String> stream();

  int size();

  boolean isEmpty();

  default void ifPresent(String tag, Consumer<String> consumer) {
    if (contains(tag)) {
      consumer.accept(tag);
    }
  }

  default void ifAbsent(String tag, Consumer<String> consumer) {
    if (!contains(tag)) {
      consumer.accept(tag);
    }
  }

  default Tags children(String parentTag) {
    Pattern pattern = Pattern.compile("^" + parentTag + "/.+");

    return from(stream()
      .filter(tag -> pattern.matcher(tag).matches())
      .collect(Collectors.toCollection(HashSet::new))
    );
  }

  static Tags newEmpty() {
    return new DefaultTagSet(new HashSet<>());
  }

  static Tags from(Set<String> tagSet) {
    return new DefaultTagSet(tagSet);
  }

  @JsonCreator
  static Tags of(String... tags) {
    return from(new HashSet<>(Arrays.asList(tags)));
  }

}

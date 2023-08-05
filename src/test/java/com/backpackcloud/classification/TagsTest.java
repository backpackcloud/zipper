package com.backpackcloud.classification;

import com.backpackcloud.serializer.Serializer;
import com.backpackcloud.spectaculous.Backstage;
import com.backpackcloud.spectaculous.TargetedAction;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagsTest {

  @Test
  public void testFlow() {
    Backstage.describe(Tags.class)
      .given(Tags.newEmpty())

      .from(Tags::size).expect(0)
      .from(Tags::isEmpty).expect(true)
      .test(contains("foo").negate())
      .test(contains("bar").negate())

      .then(add("foo"))

      .from(Tags::size).expect(1)
      .from(Tags::isEmpty).expect(false)
      .test(contains("foo"))
      .test(contains("bar").negate())

      .then(add("foo"))

      .from(Tags::size).expect(1)
      .from(Tags::isEmpty).expect(false)
      .test(contains("foo"))
      .test(contains("bar").negate())

      .then(add("bar"))

      .from(Tags::size).expect(2)
      .from(Tags::isEmpty).expect(false)
      .test(contains("foo"))
      .test(contains("bar"))

      .then(remove("bar"))

      .from(Tags::size).expect(1)
      .from(Tags::isEmpty).expect(false)
      .test(contains("foo"))
      .test(contains("bar").negate())

      .then(remove("foo"))

      .from(Tags::size).expect(0)
      .from(Tags::isEmpty).expect(true)
      .test(contains("foo").negate())
      .test(contains("bar").negate());
  }

  @Test
  public void testChildren() {
    Backstage.describe(Tags.class)
      .given(Tags.of("foo", "bar", "baz", "country", "country/br", "country/dk")
        .children("country"))

      .from(Tags::size).expect(2)
      .test(contains("country/br"))
      .test(contains("country/dk"))
      .test(contains("country").negate())
      .test(contains("foo").negate())
      .test(contains("bar").negate())
      .test(contains("baz").negate());
  }

  @Test
  public void testSerialization() {
    Serializer serializer = Serializer.json();
    Tags tags = Tags.of("foo", "bar", "baz" ,"foo/bar");
    String json = serializer.serialize(tags);

    assertEquals(tags, serializer.deserialize(json, Tags.class));
  }

  private TargetedAction<Tags> add(String tag) {
    return tags -> tags.add(tag);
  }

  private TargetedAction<Tags> remove(String tag) {
    return tags -> tags.remove(tag);
  }

  private Predicate<Tags> contains(String tag) {
    return tags -> tags.contains(tag);
  }

}

package com.backpackcloud;

import com.backpackcloud.trugger.util.ElementResolver;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElementResolverTest {

  @Test
  public void testMapResolver() {
    Map<String, Object> map = new HashMap<>();
    Map map2 = new HashMap<>();

    map.put("foo", "bar");
    map.put("bar", 10);
    map.put("baz", map2);

    map2.put("name", "zipper");

    ElementResolver resolver = ElementResolver.of(map);
    assertEquals("bar", resolver.resolve("foo"));
    assertEquals(10, resolver.resolve("bar", Integer.class));
    assertEquals("zipper", resolver.resolve("baz.name"));
  }

  @Test
  public void testObjectResolver() {
    Foo foo = new Foo();
    ElementResolver resolver = ElementResolver.of(foo);

    assertEquals("bar", resolver.resolve("field"));
    assertEquals(10, resolver.resolve("method", Integer.class));
  }

  static class Foo {

    private String field = "bar";

    public int method() {
      return 10;
    }

  }

}

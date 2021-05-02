package com.backpackcloud.zipper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InterpolatorTest {

  @Test
  public void testBasicInterpolation() {
    Function<String, Object> function = Mockito.mock(Function.class);
    when(function.apply("foo")).thenReturn("{foo bar}");
    when(function.apply("person")).thenReturn("john doe");

    Interpolator interpolator = new Interpolator(function);

    String result = interpolator.apply("[{person}]: this is a {foo} text");

    assertEquals("[john doe]: this is a {foo bar} text", result);

    verify(function).apply("foo");
    verify(function).apply("person");
  }

}

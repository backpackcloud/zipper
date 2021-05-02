package com.backpackcloud.zipper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InterpolatorTest {

  @Test
  public void testBasicInterpolation() {
    Function<String, Object> function = Mockito.mock(Function.class);
    when(function.apply("foo")).thenReturn("{lorem ipsum dolor sit amet}");
    when(function.apply("person")).thenReturn("john doe");
    when(function.apply("end")).thenReturn("!");

    Interpolator interpolator = new Interpolator(function);

    String result = interpolator.apply("[{person}] this is a {foo} text{end} [{person}]");

    assertEquals("[john doe] this is a {lorem ipsum dolor sit amet} text! [john doe]", result);

    verify(function).apply("foo");
    verify(function, times(2)).apply("person");
  }

}

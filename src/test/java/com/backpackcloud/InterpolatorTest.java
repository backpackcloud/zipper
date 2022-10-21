/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Marcelo Guimarães
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.backpackcloud;

import com.backpackcloud.text.Interpolator;
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

    String result = interpolator.eval("[{person}] this is a {foo} text{end} [{person}]").orElseThrow(AssertionError::new);

    assertEquals("[john doe] this is a {lorem ipsum dolor sit amet} text! [john doe]", result);

    verify(function).apply("foo");
    verify(function, times(2)).apply("person");
  }

  @Test
  public void testFormat() {
    Function<String, Object> function = Mockito.mock(Function.class);
    when(function.apply("foo")).thenReturn("bar");

    Interpolator interpolator = new Interpolator(function);

    String result = interpolator.eval("|{foo|-5}|").orElseThrow(UnbelievableException::new);
    assertEquals("|bar  |", result);

    result = interpolator.eval("|{foo|5}|").orElseThrow(UnbelievableException::new);
    assertEquals("|  bar|", result);

    verify(function, times(2)).apply("foo");
  }

}

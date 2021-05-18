/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Marcelo Guimarães <ataxexe@backpackcloud.com>
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

package com.backpackcloud.zipper;

import com.backpackcloud.spectaculous.Operation;
import com.backpackcloud.spectaculous.Spec;
import com.backpackcloud.zipper.impl.SelectorImpl;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SelectorTest {

  Operation<Selector, Boolean> test(LabelMap labelMap) {
    return selector -> selector.test(labelMap);
  }

  @Test
  public void testSelection() {
    LabelMap labels = LabelMap.empty();
    Predicate<LabelMap> ok = mock(Predicate.class);
    Predicate<LabelMap> notOk = mock(Predicate.class);

    when(ok.test(labels)).thenReturn(true);
    when(notOk.test(labels)).thenReturn(false);

    Spec.describe(Selector.class)

      .given(new SelectorImpl(Arrays.asList(ok, ok, ok, ok)))
      .because("All predicates should accept the label set")
      .expect(true).from(test(labels))

      .given(new SelectorImpl(Arrays.asList(ok, ok, notOk, ok)))
      .expect(false).from(test(labels))

      .given(SelectorImpl.empty())
      .because("Empty selector should match any label set")
      .expect(true).from(test(labels))
      .expect(true).from(test(LabelMap.empty()));
  }

  @Test
  public void testPredicateCreation() {
    Map<String, String> values = new HashMap<>();
    values.put("foo", "bar");
    values.put("bar", "foo");
    LabelMap labels = LabelMap.of(values);

    Spec.describe(Selector.class)

      .given(new SelectorImpl(values))

      .because("All values should be tested")
      .expect(true).from(test(labels))

      .given(selector("foo:*", "bar:*"))

      .because("Wildcard should accept any values")
      .expect(true).from(test(labels))

      .given(selector("foo:!", "bar:!"))
      .because("Exclamation mark rejects any value")
      .expect(false).from(test(labels))

      .given(selector("baz:!", "test:!"))
      .expect(true).from(test(labels))

      .given(selector("foo:baz|bar"))
      .because("Pipe should defines a set of allowed values")
      .expect(true).from(test(labels))

      .given(selector("bar:baz|bar"))
      .expect(false).from(test(labels));
  }

  private Selector selector(String... values) {
    Map map = Arrays.stream(values)
      .map(v -> {
        String[] split = v.split(":");
        return new AbstractMap.SimpleEntry(split[0], split[1]);
      })
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    return new SelectorImpl(map);
  }

}

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

package com.backpackcloud.cli.ui;

import org.jline.reader.Candidate;

import java.util.Optional;

/**
 * Represents a suggestion for the user to input in the
 * interactive interface.
 *
 * @author Marcelo "Ataxexe" Guimarães
 */
public interface Suggestion {

  /**
   * @return the value for the user to add as an input.
   */
  String value();

  /**
   * @return the optional description for this suggestion.
   */
  Optional<String> description();

  /**
   * Indicates if this suggestion is a complete term. Usually the
   * suggestions are complete terms. But in some cases like suggesting
   * files inside a directory, the user might want to go deeper in more
   * levels so it's important that the caret doesn't move to the next
   * argument.
   * <p>
   * If the suggestion is not complete, the caret should not move one space
   * for the user to input the next parameter.
   *
   * @return {@code true} if this suggestion is a complete term
   */
  boolean isComplete();

  /**
   * Suggestions can be grouped in order to provide a more organized feedback
   * for the user. Grouping suggestions will ensure the user can see the related
   * terms next to each other.
   *
   * @return the optional group which this suggestion belongs to.
   */
  Optional<String> group();

  default Candidate toCandidate() {
    return new Candidate(
      this.value(),
      this.value(),
      this.group().orElse(null),
      this.description().orElse(null),
      null,
      null,
      this.isComplete()
    );
  }

}

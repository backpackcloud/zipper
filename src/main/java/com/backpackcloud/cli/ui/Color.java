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

/**
 * A basic representation of a color.
 *
 * @author Marcelo "Ataxexe" Guimarães
 */
public class Color {

  private final int red;
  private final int green;
  private final int blue;

  /**
   * Creates a new Color using RGB values.
   *
   * @param red   the amount of red
   * @param green the amount of green
   * @param blue  the amount of blue
   */
  public Color(int red, int green, int blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public int red() {
    return red;
  }

  public int green() {
    return green;
  }

  public int blue() {
    return blue;
  }

  public int toInt() {
    return red << 16 | green << 8 | blue;
  }

  @Override
  public String toString() {
    return String.format("%s%s%s",
      Integer.toString(red, 16),
      Integer.toString(green, 16),
      Integer.toString(blue, 16));
  }

  /**
   * Parses an RGB representation of 6 hex digits.
   *
   * @param rgb the rgb representation
   * @return the parsed color
   */
  public static Color parse(String rgb) {
    int red = Integer.parseInt(rgb.substring(0, 2), 16);
    int green = Integer.parseInt(rgb.substring(2, 4), 16);
    int blue = Integer.parseInt(rgb.substring(4, 6), 16);

    return new Color(red, green, blue);
  }

}

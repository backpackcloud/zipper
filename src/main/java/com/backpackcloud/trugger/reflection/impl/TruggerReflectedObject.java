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

package com.backpackcloud.trugger.reflection.impl;

import com.backpackcloud.trugger.reflection.ReflectedObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

public abstract class TruggerReflectedObject<E extends Member> implements ReflectedObject<E> {

  private final AnnotatedElement object;
  private final Member member;

  public TruggerReflectedObject(AnnotatedElement object) {
    this.object = object;
    this.member = (Member) object;
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    return this.object.getAnnotation(annotationClass);
  }

  @Override
  public Annotation[] getAnnotations() {
    return this.object.getAnnotations();
  }

  @Override
  public Annotation[] getDeclaredAnnotations() {
    return this.object.getDeclaredAnnotations();
  }

  @Override
  public Class<?> getDeclaringClass() {
    return member.getDeclaringClass();
  }

  @Override
  public String getName() {
    return member.getName();
  }

  @Override
  public int getModifiers() {
    return member.getModifiers();
  }

  @Override
  public boolean isSynthetic() {
    return member.isSynthetic();
  }

}

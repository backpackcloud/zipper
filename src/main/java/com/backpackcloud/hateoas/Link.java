package com.backpackcloud.hateoas;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Repeatable(Links.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Link {

  String rel();

  String title() default "";

  String uri();
  
}

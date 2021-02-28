package com.backpackcloud.zipper.api.hateoas;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Link {

  String title() default "";

  String rel() default "";

}

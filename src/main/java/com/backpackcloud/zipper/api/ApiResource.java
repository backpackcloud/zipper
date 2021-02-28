package com.backpackcloud.zipper.api;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiResource {

  Class<? extends ApiResourceModel> model();

}

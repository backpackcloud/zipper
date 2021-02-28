package com.backpackcloud.zipper.api;

import com.backpackcloud.zipper.api.hateoas.EntityModel;
import com.backpackcloud.zipper.domain.Entity;

public class ApiResultWrapper<E extends Entity> {

  private final String basePath;

  public ApiResultWrapper(String basePath) {
    this.basePath = basePath;
  }

  public EntityModel<E> wrap(E entity, PropertyFilter filter) {
    return new EntityModel<>(entity, filter);
  }

}

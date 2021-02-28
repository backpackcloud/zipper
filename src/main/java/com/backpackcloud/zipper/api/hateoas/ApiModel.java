package com.backpackcloud.zipper.api.hateoas;

import com.backpackcloud.zipper.domain.Entity;

public interface ApiModel<E extends Entity> {

  Entity entity();

  LinkMapper<ApiModel<E>> link(String uri);

  default LinkMapper<ApiModel<E>> link(String uriFormat, Object... args) {
    return link(String.format(uriFormat, args));
  }

}

package com.backpackcloud.zipper.api.hateoas;

import com.backpackcloud.zipper.domain.Entity;

import java.net.URI;
import java.util.Optional;

public interface ApiModel<E extends Entity> {

  Entity entity();

  LinkMapper<ApiModel<E>> link(String uri);

  Optional<ApiLink> linkTo(String rel);

  default LinkMapper<ApiModel<E>> link(String uriFormat, Object... args) {
    return link(String.format(uriFormat, args));
  }

}

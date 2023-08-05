package com.backpackcloud.hateoas;

import com.backpackcloud.hateoas.impl.CollectionModel;

import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Optional;

public interface ApiCollectionModel<E> {

  Collection<ApiModel<E>> values();

  LinkMapper<ApiCollectionModel<E>> link(String uri);

  Optional<ApiLink> linkTo(String rel);

  default LinkMapper<ApiCollectionModel<E>> link(String uriFormat, Object... args) {
    return link(String.format(uriFormat, args));
  }

  default Response toResponse() {
    if (values().isEmpty()) return Response.status(404).build();
    return toResponse(200);
  }

  default Response toResponse(int status) {
    return Response.status(status).entity(this).build();
  }

  static <E> ApiCollectionModel<E> from(Collection<E> values) {
    return new CollectionModel<E>(values);
  }

  static <E> ApiCollectionModel<E> from(Collection<E> values, long total) {
    return new CollectionModel<E>(values, total);
  }

}

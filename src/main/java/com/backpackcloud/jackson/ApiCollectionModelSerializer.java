package com.backpackcloud.jackson;

import com.backpackcloud.hateoas.ApiCollectionModel;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Collection;

public class ApiCollectionModelSerializer<E extends Collection> extends JsonSerializer<E> {

  @Override
  public void serialize(Collection o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    serializerProvider.defaultSerializeValue(ApiCollectionModel.from(o), jsonGenerator);
  }

}

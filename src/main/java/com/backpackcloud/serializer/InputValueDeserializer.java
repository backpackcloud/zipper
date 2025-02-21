package com.backpackcloud.serializer;

import com.backpackcloud.text.InputValue;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class InputValueDeserializer extends JsonDeserializer<InputValue> {

  @Override
  public InputValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
    JsonNode jsonNode = ctxt.readTree(p);
    return InputValue.of(jsonNode.at("").asText());
  }

}

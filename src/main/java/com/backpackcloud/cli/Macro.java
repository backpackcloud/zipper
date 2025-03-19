package com.backpackcloud.cli;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Macro(@JsonProperty("name") String name,
                    @JsonProperty("description") String description,
                    @JsonProperty("commands") List<String> commands) {

}

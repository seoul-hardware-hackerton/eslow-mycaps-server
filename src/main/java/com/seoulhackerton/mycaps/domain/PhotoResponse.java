package com.seoulhackerton.mycaps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"result"})
public class PhotoResponse {

    @JsonProperty("ok")
    Boolean working;
}

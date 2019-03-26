package com.seoulhackerton.mycaps.domain.Image;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Captions {

    @JsonProperty("confidence")
    Float confidence;
    @JsonProperty("text")
    String text;

    @Override
    public String toString() {
        return "Captions{" +
                "confidence=" + confidence +
                ", text='" + text + '\'' +
                '}';
    }
}

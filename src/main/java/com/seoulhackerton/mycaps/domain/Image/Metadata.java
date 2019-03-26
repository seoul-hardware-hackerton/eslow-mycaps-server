package com.seoulhackerton.mycaps.domain.Image;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Metadata {

    //     "width": 1826,
//             "format": "Jpeg",
//             "height": 2436
    @JsonProperty("width")
    int width;
    @JsonProperty("format")
    String format;
    @JsonProperty("height")
    int height;

    @Override
    public String toString() {
        return "Metadata{" +
                "width=" + width +
                ", format='" + format + '\'' +
                ", height=" + height +
                '}';
    }
}

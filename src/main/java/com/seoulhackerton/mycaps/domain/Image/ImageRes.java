package com.seoulhackerton.mycaps.domain.Image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties({"categories"})
public class ImageRes {

    @JsonProperty("metadata")
    @JsonSerialize(using = MetadataSerializer.class)
    Metadata metadata;
    @JsonProperty("color")
    @JsonSerialize(using = ColorSerializer.class)
    Color color;
    @JsonProperty("requestId")
    String requestId;
    @JsonProperty("description")
    @JsonSerialize(using = DescriptionSerializer.class)
    Description description;

    @JsonProperty("message")
    String message;

    @Override
    public String toString() {
        return "ImageRes{" +
                "metadata=" + metadata +
                ", color=" + color +
                ", requestId=" + requestId +
                ", description=" + description +
                '}';
    }
}

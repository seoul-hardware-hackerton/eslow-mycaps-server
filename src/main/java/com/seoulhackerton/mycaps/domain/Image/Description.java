package com.seoulhackerton.mycaps.domain.Image;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Description {

    @JsonProperty("tags")
    ArrayList<String> tags;

    @JsonProperty("captions")
    ArrayList<Captions> captions;

    @Override
    public String toString() {
        return "Description{" +
                "tags=" + tags +
                ", captions=" + captions +
                '}';
    }
}

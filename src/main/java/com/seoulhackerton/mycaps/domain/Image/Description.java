package com.seoulhackerton.mycaps.domain.Image;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Description {

    @JsonProperty("tags")
    ArrayList<String> tags;

    @JsonProperty("captions")
    ArrayList<Captions> captions;

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<Captions> getCaptions() {
        return captions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Result Tags: ");
        for (String tag : tags) {
            sb.append(tag).append(", ");
        }
        return sb.toString();
    }
}

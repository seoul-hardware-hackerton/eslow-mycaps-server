package com.seoulhackerton.mycaps.domain.Image;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Color {

    @JsonProperty("dominantColorForeground")
    String dominantColorForeground;
    @JsonProperty("dominantColorBackground")
    String dominantColorBackground;
    @JsonProperty("isBwImg")
    boolean isBwImg;
    @JsonProperty("isBWImg")
    boolean isBWImg;
    @JsonProperty("accentColor")
    String accentColor;
    @JsonProperty("dominantColors")
    ArrayList<String> dominantColors;

    @Override
    public String toString() {
        return "Color{" +
                "dominantColorForeground='" + dominantColorForeground + '\'' +
                ", dominantColorBackground='" + dominantColorBackground + '\'' +
                ", isBWImg=" + isBwImg +
                ", accentColor='" + accentColor + '\'' +
                ", dominantColors=" + dominantColors +
                '}';
    }
}

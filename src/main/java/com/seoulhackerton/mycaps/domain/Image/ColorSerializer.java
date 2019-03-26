package com.seoulhackerton.mycaps.domain.Image;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ColorSerializer extends JsonSerializer<Color> {
    @Override
    public void serialize(Color color, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if (jsonGenerator.canWriteObjectId()) {
            jsonGenerator.writeObject(color);
        }
    }
}

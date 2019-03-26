package com.seoulhackerton.mycaps.domain.Image;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class MetadataSerializer extends JsonSerializer<Metadata> {
    @Override
    public void serialize(Metadata metadata, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(metadata);
    }
}

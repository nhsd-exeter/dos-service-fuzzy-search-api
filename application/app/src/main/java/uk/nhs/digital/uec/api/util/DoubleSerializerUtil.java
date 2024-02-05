package uk.nhs.digital.uec.api.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;


public class DoubleSerializerUtil extends JsonSerializer<Double> {

  @Override
  public void serialize(Double aDouble, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(String.format("%.10f", aDouble));
  }
}

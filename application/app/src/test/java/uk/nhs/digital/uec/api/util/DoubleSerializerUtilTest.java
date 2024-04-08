package uk.nhs.digital.uec.api.util;

import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DoubleSerializerUtilTest {

  @Test
  public void serializesDoubleCorrectly() throws IOException {
    JsonGenerator jsonGenerator = mock(JsonGenerator.class);

    DoubleSerializerUtil classUnderTest = new DoubleSerializerUtil();
    classUnderTest.serialize(1.0, jsonGenerator, null);
    verify(jsonGenerator).writeString("1.0000000000");
  }

}

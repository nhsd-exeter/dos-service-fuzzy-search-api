package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiValidationErrorResponseTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testSerializesCorrectly() throws JsonProcessingException {
    ApiValidationErrorResponse response = new ApiValidationErrorResponse("validationCode", "validationError");
    String json = objectMapper.writeValueAsString(response);
    assertEquals("{\"validation_code\":\"validationCode\",\"validation_error\":\"validationError\"}", json);
  }

}

package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import javax.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
@ExtendWith(SpringExtension.class)
public class FuzzySearchHomeControllerTest {

private String apiVersion = "v0.0.3";

  @InjectMocks FuzzySearchHomeController fuzzyServiceHomeController;

  @BeforeEach
  public void setup() {
    ReflectionTestUtils.setField(fuzzyServiceHomeController, "apiVersion", apiVersion);
  }

  @Test
  public void homeEndpointTest() throws ValidationException {
    String response = fuzzyServiceHomeController.home();
    assertEquals("This is the DoS Service Fuzzy Search API. Version: " + apiVersion, response);

  }
}

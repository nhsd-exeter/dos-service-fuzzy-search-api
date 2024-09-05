package uk.nhs.digital.uec.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.nhs.digital.uec.api.controller.FuzzyServiceSearchController;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.ErrorMessage;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.DosServiceSearch;

@Slf4j
@TestPropertySource(
    properties = {
      "LOG_LEVEL=TRACE",
      "configuration.search_parameters.max_num_services_to_return=2",
      "configuration.search_parameters.fuzz_level=0",
    })
@SpringBootTest(
    classes = {
      FuzzySearchControllerAdviceTest.Config.class,
      FuzzySearchControllerAdviceTest.class,
      FuzzyServiceSearchController.class,
      FuzzySearchControllerAdvice.class,
    })
public class FuzzySearchControllerAdviceTest {

  @Autowired FuzzyServiceSearchController controller;

  @MockBean DosServiceSearch fuzzyServiceSearchService;

  @MockBean ApiUtilsServiceInterface utils;

  @MockBean ApiRequestParams requestParams;

  @TestConfiguration
  static class Config {

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new FuzzySearchControllerAdvice())
            .build();
  }

  @Test
  public void testNotFound() throws Exception {
    when(fuzzyServiceSearchService.retrieveServicesByGeoLocation(any(), any(), any(), any(), any()))
        .thenThrow(new NotFoundException("Not found"));

    mockMvc
        .perform(
            get("/dosapi/dosservices/v0.0.1/services/byfuzzysearch")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\"message\":\"Not found\"}"));
  }

  @Test
  public void testInvalidParameterException() throws Exception {
    when(fuzzyServiceSearchService.retrieveServicesByGeoLocation(any(), any(), any(), any(), any()))
        .thenThrow(
            new InvalidParameterException(
                "None of the search criteria given meets the minimum required search criteria"
                    + " length."));

    mockMvc
        .perform(
            get("/dosapi/dosservices/v0.0.1/services/byfuzzysearch")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().is4xxClientError())
        .andExpect(
            content()
                .json(
                    "{\"validationCode\":\"PMA-VAL003\",\"message\":\"None of the search criteria"
                        + " given meets the minimum required search criteria length.\"}"));
  }

  @Test
  /*
  I can't find a way that a normal checked exception would be thrown and dealt with by the ControllerAdvice,
  so this will be tested directly
  */
  public void testNormalException() throws Exception {
    FuzzySearchControllerAdvice advice = new FuzzySearchControllerAdvice();
    ResponseEntity<ErrorMessage> errorResponse =
        advice.handleException(new Exception("An internal test error has occurred"));
    assertEquals("An internal test error has occurred", errorResponse.getBody().getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getBody().getStatus());
  }
}

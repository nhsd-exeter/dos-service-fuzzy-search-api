package uk.nhs.digital.uec.api.integration.fuzzysearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.digital.uec.api.model.ApiValidationErrorResponse;
import uk.nhs.digital.uec.api.util.PropertySourceResolver;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
/**
 * Test class which passes requests through the Fuzzy Search endpoint and asserts desired API
 * behavior. Only the model layer will be mocked here.
 */
public class FuzzySearchValidationErrorsTest {

  @Autowired private ObjectMapper mapper;

  @Autowired private PropertySourceResolver propertySourceResolver;

  private static String endpointUrl;

  TestRestTemplate restTemplate = new TestRestTemplate();
  HttpHeaders headers = new HttpHeaders();

  @BeforeEach
  public void configureProperties() {
    endpointUrl = propertySourceResolver.endpointUrl;
  }

  /**
   * VAL-001 Given no search criteria terms, ensure that the API returns a 400 error with the
   * VAL-001 validation code.
   */
  @Test
  public void noSearchCriteriaGiven() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(endpointUrl, HttpMethod.GET, request, String.class);

    assertTrue(responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST);

    ApiValidationErrorResponse response =
        mapper.readValue(responseEntity.getBody(), ApiValidationErrorResponse.class);

    assertEquals(response.getValidationCode(), "VAL-001");
  }

  /**
   * VAL-002 Given too many search criteria terms, ensure that the API returns a 400 error with the
   * VAL-002 validation code.
   */
  @Test
  public void tooManySearchCriteriaGiven() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl)
            .queryParam("search_criteria", "Term1")
            .queryParam("search_criteria", "Term2")
            .queryParam("search_criteria", "Term3")
            .queryParam("search_criteria", "Term4")
            .queryParam("search_criteria", "Term5")
            .queryParam("search_criteria", "Term6");
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST);

    ApiValidationErrorResponse response =
        mapper.readValue(responseEntity.getBody(), ApiValidationErrorResponse.class);

    assertEquals(response.getValidationCode(), "VAL-002");
  }

  /**
   * VAL-003 Given no criteria terms that fullfil the min character quota, ensure that the API
   * returns a 400 error with the VAL-003 validation code.
   */
  @Test
  public void noSearchTermMeetsRequirements() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl)
            .queryParam("search_criteria", "1")
            .queryParam("search_criteria", "ab")
            .queryParam("search_criteria", "z");
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST);

    ApiValidationErrorResponse response =
        mapper.readValue(responseEntity.getBody(), ApiValidationErrorResponse.class);

    assertEquals(response.getValidationCode(), "VAL-003");
  }
}

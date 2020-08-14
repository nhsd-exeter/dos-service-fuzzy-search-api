package uk.nhs.digital.uec.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.nhs.digital.uec.api.model.ApiSuccResponse;
import uk.nhs.digital.uec.api.model.ApiValidationErrorResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles({"prod"})
/**
 * Test class which passes requests through the Fuzzy Search endpoint and asserts desired API
 * behavior. Only the model layer will be mocked here.
 */
public class FuzzySearchTest {

  @Autowired private ObjectMapper mapper;

  private static String endpointUrl =
      "http://localhost:9095/dosapi/dosservices/v0.0.1/services/byfuzzysearch";

  TestRestTemplate restTemplate = new TestRestTemplate();
  HttpHeaders headers = new HttpHeaders();

  /** Sunny day scenarios. */

  /**
   * Given valid search criteria terms, ensure the API returns a 200 success code, and a list of
   * services are returned.
   *
   * @throws Exception
   */
  @Test
  public void oneSearchCriteriaGiven() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl).queryParam("search_criteria", "Term1");
    ResponseEntity<String> response =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(response.getStatusCode() == HttpStatus.OK);

    ApiSuccResponse apiSuccResponse = mapper.readValue(response.getBody(), ApiSuccResponse.class);

    assertEquals(apiSuccResponse.getNumberOfServices(), 2);
  }

  /**
   * Given at least one valid search term, ensure the API returns a 200 success code, and a list of
   * services are returned.
   *
   * @throws Exception
   */
  @Test
  public void atLeastOneValidSearchTermGiven() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl)
            .queryParam("search_criteria", "a")
            .queryParam("search_criteria", "ab")
            .queryParam("search_criteria", "Term1");
    ResponseEntity<String> response =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(response.getStatusCode() == HttpStatus.OK);

    ApiSuccResponse apiSuccResponse = mapper.readValue(response.getBody(), ApiSuccResponse.class);

    assertEquals(apiSuccResponse.getNumberOfServices(), 2);
  }

  /**
   * Given valid search criteria terms, ensure the API returns a 200 success code, and a list of no
   * services are returned.
   *
   * @throws Exception
   */
  @Test
  public void oneSearchCriteriaGivenNoResults() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl).queryParam("search_criteria", "Term0");
    ResponseEntity<String> response =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(response.getStatusCode() == HttpStatus.OK);

    ApiSuccResponse apiSuccResponse = mapper.readValue(response.getBody(), ApiSuccResponse.class);

    assertEquals(apiSuccResponse.getNumberOfServices(), 0);
  }

  /** HTTP 405 Errors */

  /** Given a Post call on the endpoint, ensure a 405 error is returned. */
  @Test
  public void postCallOnEndpoint() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    ResponseEntity<String> response =
        restTemplate.exchange(endpointUrl, HttpMethod.POST, request, String.class);

    assertTrue(response.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED);
  }

  /** Given a Patch call on the endpoint, ensure a 405 error is returned. */
  @Test
  public void patchCallOnEndpoint() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    ResponseEntity<String> response =
        restTemplate.exchange(endpointUrl, HttpMethod.PATCH, request, String.class);

    assertTrue(response.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED);
  }

  /** Given a Put call on the endpoint, ensure a 405 error is returned. */
  @Test
  public void putCallOnEndpoint() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    ResponseEntity<String> response =
        restTemplate.exchange(endpointUrl, HttpMethod.PUT, request, String.class);

    assertTrue(response.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED);
  }

  /** Given a Delete call on the endpoint, ensure a 405 error is returned. */
  @Test
  public void deleteCallOnEndpoint() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    ResponseEntity<String> response =
        restTemplate.exchange(endpointUrl, HttpMethod.DELETE, request, String.class);

    assertTrue(response.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED);
  }

  /** Validation Errors (HTTP-400), plus our internal validation codes (VAL-XXX) */

  /**
   * VAL-001 Given no search criteria terms, ensure that the API returns a 400 error with the
   * VAL-001 validation code.
   */
  @Test
  public void noSearchCriteriaGiven() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    ResponseEntity<String> response =
        restTemplate.exchange(endpointUrl, HttpMethod.GET, request, String.class);

    assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);

    ApiValidationErrorResponse apiValidationErrorResponse =
        mapper.readValue(response.getBody(), ApiValidationErrorResponse.class);

    assertEquals(apiValidationErrorResponse.getValidationCode(), "VAL-001");
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
    ResponseEntity<String> response =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);

    ApiValidationErrorResponse apiValidationErrorResponse =
        mapper.readValue(response.getBody(), ApiValidationErrorResponse.class);

    assertEquals(apiValidationErrorResponse.getValidationCode(), "VAL-002");
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
    ResponseEntity<String> response =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);

    ApiValidationErrorResponse apiValidationErrorResponse =
        mapper.readValue(response.getBody(), ApiValidationErrorResponse.class);

    assertEquals(apiValidationErrorResponse.getValidationCode(), "VAL-003");
  }
}

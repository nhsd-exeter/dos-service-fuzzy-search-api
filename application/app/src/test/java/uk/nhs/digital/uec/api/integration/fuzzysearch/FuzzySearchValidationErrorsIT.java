package uk.nhs.digital.uec.api.integration.fuzzysearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.digital.uec.api.model.ApiValidationErrorResponse;

/**
 * Test class which passes requests through the Fuzzy Search endpoint and asserts desired API
 * behavior. Only the model layer will be mocked here.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FuzzySearchValidationErrorsIT extends AuthServiceIT {

  private String endpointUrl;

  MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

  private final String SEARCH_TERM = "search_term";

  @BeforeEach
  public void configureProperties() throws Exception {
    endpointUrl = getEndpointUrl();
    headers = getAuthorizedHeader();
  }

  /**
   * VAL-001 Given no search criteria terms, ensure that the API returns a 400 error with the
   * VAL-001 validation code.
   */
  @Test
  public void noSearchCriteriaGiven() throws Exception {
    // Arrange
    HttpEntity<String> request = new HttpEntity<>(null, headers);

    // Act
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(endpointUrl, HttpMethod.GET, request, String.class);

    // Assert
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
    // Arrange
    HttpEntity<String> request = new HttpEntity<>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl)
            .queryParam(SEARCH_TERM, "Term1")
            .queryParam(SEARCH_TERM, "Term2")
            .queryParam(SEARCH_TERM, "Term3")
            .queryParam(SEARCH_TERM, "Term4")
            .queryParam(SEARCH_TERM, "Term5")
            .queryParam(SEARCH_TERM, "Term6");

    // Act
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    // Assert
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
    // Arrange
    HttpEntity<String> request = new HttpEntity<>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl)
            .queryParam(SEARCH_TERM, "1")
            .queryParam(SEARCH_TERM, "ab")
            .queryParam(SEARCH_TERM, "z");

    // Act
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    // Assert
    assertTrue(responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST);

    ApiValidationErrorResponse response =
        mapper.readValue(responseEntity.getBody(), ApiValidationErrorResponse.class);

    assertEquals(response.getValidationCode(), "VAL-003");
  }
}

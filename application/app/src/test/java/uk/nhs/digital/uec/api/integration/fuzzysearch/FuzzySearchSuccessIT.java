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
import uk.nhs.digital.uec.api.model.ApiSuccessResponse;

/**
 * Test class which passes requests through the Fuzzy Search endpoint and asserts desired API
 * behavior. Only the model layer will be mocked here.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FuzzySearchSuccessIT extends AuthServiceIT {

  private String endpointUrl;
  private static final String SEARCH_TERM = "search_term";
  private static final String SERVICE_1 = "service1";

  MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

  @BeforeEach
  public void configureProperties() throws Exception {
    endpointUrl = getEndpointUrl();
    headers = getAuthorizedHeader();
  }

  /** Sunny day scenarios. */

  /**
   * Given valid search criteria terms, ensure the API returns a 200 success code, and a list of
   * services are returned.
   *
   * @throws Exception
   */
  @Test
  public void oneSearchCriteriaGiven() throws Exception {
    // Arrange
    HttpEntity<String> request = new HttpEntity<>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl).queryParam(SEARCH_TERM, SERVICE_1);

    // Act
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    // Assert
    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

    ApiSuccessResponse response =
        mapper.readValue(responseEntity.getBody(), ApiSuccessResponse.class);

    assertEquals(1, response.getSearchResults().getNumberOfServicesFound());
    assertEquals(SERVICE_1, response.getSearchResults().getServices().get(0).getName());
  }

  /**
   * Given at least one valid search term, ensure the API returns a 200 success code, and a list of
   * services are returned.
   *
   * @throws Exception
   */
  @Test
  public void atLeastOneValidSearchTermGiven() throws Exception {
    // Arrange
    HttpEntity<String> request = new HttpEntity<>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl)
            .queryParam(SEARCH_TERM, "a")
            .queryParam(SEARCH_TERM, "ab")
            .queryParam(SEARCH_TERM, SERVICE_1);

    // Act
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    // Assert
    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

    ApiSuccessResponse response =
        mapper.readValue(responseEntity.getBody(), ApiSuccessResponse.class);

    assertEquals(1, response.getSearchResults().getNumberOfServicesFound());
    assertEquals(SERVICE_1, response.getSearchResults().getServices().get(0).getName());
  }

  /**
   * Given valid search criteria terms, ensure the API returns a 200 success code, and a list of no
   * services are returned.
   *
   * @throws Exception
   */
  @Test
  public void oneSearchCriteriaGivenNoResults() throws Exception {
    // Arrange
    HttpEntity<String> request = new HttpEntity<>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl).queryParam(SEARCH_TERM, "Expect No Results");

    // Act
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    // Assert
    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

    ApiSuccessResponse response =
        mapper.readValue(responseEntity.getBody(), ApiSuccessResponse.class);

    assertEquals(0, response.getSearchResults().getNumberOfServicesFound());
  }

  /** Given search criteria of ALL, return the maximum number of services. */
  @Test
  public void allSearchCriteriaGivenResults() throws Exception {
    // Arrange
    HttpEntity<String> request = new HttpEntity<>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl)
            .queryParam(SEARCH_TERM, "service")
            .queryParam("fuzz_level", 1);

    // Act
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    // Assert
    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

    ApiSuccessResponse response =
        mapper.readValue(responseEntity.getBody(), ApiSuccessResponse.class);

    assertTrue(response.getSearchResults().getNumberOfServicesFound() > 1);
  }
}

package uk.nhs.digital.uec.api.integration.fuzzysearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.digital.uec.api.model.ApiSuccessResponse;
import uk.nhs.digital.uec.api.util.PropertySourceResolver;

/**
 * Test class which passes requests through the Fuzzy Search endpoint and asserts desired API
 * behavior. Only the model layer will be mocked here.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled
public class FuzzySearchSuccessTest {

  @Autowired private ObjectMapper mapper;

  @Autowired private PropertySourceResolver propertySourceResolver;

  @Autowired private TestRestTemplate restTemplate;

  private static String endpointUrl;

  MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

  @BeforeEach
  public void configureProperties() throws Exception {
    endpointUrl = propertySourceResolver.endpointUrl;
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJpZCIsImlhdCI6MTYzMjMwNzYzNiwic3ViIjoiYWRtaW5AbmhzLm5ldCIsImlzcyI6Imlzc3VlciIsImV4cCI6MTYzMjMxMTIzNiwiY29nbml0bzpncm91cHMiOlsiQVBJX1VTRVIiXX0.ImG80nV8Amb9Q4pZrcGm6cYnfSo7onHy_q0E_7u6w8muUOdDQOcmgPpfmdqsu8fhlYGouEhR8y5UWctp-LT-cFuoquVFELY-IeM02zj7hRVqNrSvhATeVUai8xUHcq7cxmf6IiWcrSwcRSN9oA1F6pc3E0q88Kk3nd_ieHMZfGbbbs1DkycqsR-KIGkymVN0W527meBM_9P8PMknfzvYWUJh4GV_h47V0Fx_e9R6lOTH0NfRYWc1nSM3q8cU6RkypF-Uyq9dHkcuiYlwwBYqRenoHbFbLsYFdGUby6OMwERbKlUvdCXyJB_yyp7-QVKRuSetaYCWksJUqp5pdxujXw");
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
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl).queryParam("search_term", "service1");

    // Act
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    // Assert
    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

    ApiSuccessResponse response =
        mapper.readValue(responseEntity.getBody(), ApiSuccessResponse.class);

    assertEquals(1, response.getSearchResults().getNumberOfServicesFound());
    assertEquals("service1", response.getSearchResults().getServices().get(0).getName());
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
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl)
            .queryParam("search_term", "a")
            .queryParam("search_term", "ab")
            .queryParam("search_term", "service1");

    // Act
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    // Assert
    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

    ApiSuccessResponse response =
        mapper.readValue(responseEntity.getBody(), ApiSuccessResponse.class);

    assertEquals(1, response.getSearchResults().getNumberOfServicesFound());
    assertEquals("service1", response.getSearchResults().getServices().get(0).getName());
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
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl)
            .queryParam("search_term", "Expect No Results");

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
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl)
            .queryParam("search_term", "service")
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

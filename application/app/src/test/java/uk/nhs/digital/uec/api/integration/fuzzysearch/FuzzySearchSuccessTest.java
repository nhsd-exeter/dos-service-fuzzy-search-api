package uk.nhs.digital.uec.api.integration.fuzzysearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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
import uk.nhs.digital.uec.api.model.ApiSuccessResponse;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;
import uk.nhs.digital.uec.api.util.PropertySourceResolver;

/**
 * Test class which passes requests through the Fuzzy Search endpoint and asserts desired API
 * behavior. Only the model layer will be mocked here.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class FuzzySearchSuccessTest {

  @Autowired private ObjectMapper mapper;

  @Autowired private PropertySourceResolver propertySourceResolver;

  private static String endpointUrl;
  private static int maxNoServicesToReturn;

  TestRestTemplate restTemplate = new TestRestTemplate();
  HttpHeaders headers = new HttpHeaders();

  @BeforeEach
  public void configureProperties() {
    endpointUrl = propertySourceResolver.endpointUrl;
    maxNoServicesToReturn = propertySourceResolver.maxNumServicesToReturn;
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
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl).queryParam("search_criteria", "Term1");
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

    ApiSuccessResponse response =
        mapper.readValue(responseEntity.getBody(), ApiSuccessResponse.class);

    assertEquals(response.getNumberOfServices(), 2);

    // Add the service ids that you want to verify
    List<Integer> serviceIdsToVerify = new ArrayList<>();
    serviceIdsToVerify.add(1);
    serviceIdsToVerify.add(2);

    verifySuccessResponse(response, serviceIdsToVerify);
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
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

    ApiSuccessResponse response =
        mapper.readValue(responseEntity.getBody(), ApiSuccessResponse.class);

    assertEquals(response.getNumberOfServices(), 2);

    // Add the service ids that you want to verify
    List<Integer> serviceIdsToVerify = new ArrayList<>();
    serviceIdsToVerify.add(1);
    serviceIdsToVerify.add(2);

    verifySuccessResponse(response, serviceIdsToVerify);
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
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

    ApiSuccessResponse response =
        mapper.readValue(responseEntity.getBody(), ApiSuccessResponse.class);

    assertEquals(response.getNumberOfServices(), 0);
  }

  /** Given search criteria of ALL, return the maximum number of services. */
  @Test
  public void allSearchCriteriaGivenMaxResults() throws Exception {
    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl).queryParam("search_criteria", "All");
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

    ApiSuccessResponse response =
        mapper.readValue(responseEntity.getBody(), ApiSuccessResponse.class);

    assertEquals(response.getNumberOfServices(), maxNoServicesToReturn);
  }

  /** Method to verify the service details that are returned from the API call. */
  private void verifySuccessResponse(
      ApiSuccessResponse response, List<Integer> serviceIdsToVerify) {

    for (Integer serviceIdToVerify : serviceIdsToVerify) {

      DosService dosServiceToCheckAgainst =
          MockDosServicesUtil.mockDosServices.get(serviceIdToVerify);

      for (DosService dosServiceToVerify : response.getServices()) {
        if (dosServiceToVerify.getId() == serviceIdToVerify) {
          assertTrue(dosServiceToCheckAgainst.equals(dosServiceToVerify));
          break;
        }
      }
    }
  }
}

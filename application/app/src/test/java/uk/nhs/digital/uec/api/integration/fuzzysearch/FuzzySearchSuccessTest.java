package uk.nhs.digital.uec.api.integration.fuzzysearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.utils.MockDosServicesUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles({"prod"})
/**
 * Test class which passes requests through the Fuzzy Search endpoint and asserts desired API
 * behavior. Only the model layer will be mocked here.
 */
public class FuzzySearchSuccessTest {

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

    // Add the service ids that you want to verify
    List<Integer> serviceIdsToVerify = new ArrayList<>();
    serviceIdsToVerify.add(1);
    serviceIdsToVerify.add(2);

    verifySuccessResponse(apiSuccResponse, serviceIdsToVerify);
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

    // Add the service ids that you want to verify
    List<Integer> serviceIdsToVerify = new ArrayList<>();
    serviceIdsToVerify.add(1);
    serviceIdsToVerify.add(2);

    verifySuccessResponse(apiSuccResponse, serviceIdsToVerify);
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

  /** Method to verify the service details that are returned from the API call. */
  private void verifySuccessResponse(
      ApiSuccResponse apiSuccResponse, List<Integer> serviceIdsToVerify) {

    for (Integer serviceIdToVerify : serviceIdsToVerify) {

      DosService dosServiceToCheckAgainst =
          MockDosServicesUtil.mockDosServices.get(serviceIdToVerify);

      for (DosService dosServiceToVerify : apiSuccResponse.getServices()) {
        if (dosServiceToVerify.getId() == serviceIdToVerify) {
          assertEquals(
              dosServiceToVerify.getUIdentifier(), dosServiceToCheckAgainst.getUIdentifier());
          assertEquals(dosServiceToVerify.getName(), dosServiceToCheckAgainst.getName());
          assertEquals(
              dosServiceToVerify.getPublicName(), dosServiceToCheckAgainst.getPublicName());
          assertEquals(dosServiceToVerify.getType(), dosServiceToCheckAgainst.getType());
          assertEquals(dosServiceToVerify.getTypeId(), dosServiceToCheckAgainst.getTypeId());
          assertEquals(dosServiceToVerify.getOdsCode(), dosServiceToCheckAgainst.getOdsCode());
          assertEquals(
              dosServiceToVerify.getCapacityStatus(), dosServiceToCheckAgainst.getCapacityStatus());

          assertEquals(
              dosServiceToVerify.getAddress().size(), dosServiceToCheckAgainst.getAddress().size());
          for (String addressLineToCheck : dosServiceToCheckAgainst.getAddress()) {
            assertTrue(dosServiceToVerify.getAddress().contains(addressLineToCheck));
          }
          assertEquals(dosServiceToVerify.getPostcode(), dosServiceToCheckAgainst.getPostcode());

          assertEquals(
              dosServiceToVerify.getReferralRoles().size(),
              dosServiceToCheckAgainst.getReferralRoles().size());
          for (String referralRoleToCheck : dosServiceToCheckAgainst.getReferralRoles()) {
            assertTrue(dosServiceToVerify.getReferralRoles().contains(referralRoleToCheck));
          }

          break;
        }
      }
    }
  }
}

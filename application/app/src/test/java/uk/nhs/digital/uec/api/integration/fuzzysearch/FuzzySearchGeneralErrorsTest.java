package uk.nhs.digital.uec.api.integration.fuzzysearch;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
import uk.nhs.digital.uec.api.util.PropertySourceResolver;

/**
 * Test class which passes request import org.springframework.util.MultiValueMap;
 *
 * <p>through the Fuzzy Search endpoint and asserts desired API behavior. Only the model layer will
 * be mocked here.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled
public class FuzzySearchGeneralErrorsTest {

  @Autowired private PropertySourceResolver propertySourceResolver;
  @Autowired public TestRestTemplate restTemplate;

  private static String endpointUrl;

  MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

  @BeforeEach
  public void configureProperties() throws Exception {
    endpointUrl = propertySourceResolver.endpointUrl;
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJpZCIsImlhdCI6MTYzMjMwNzYzNiwic3ViIjoiYWRtaW5AbmhzLm5ldCIsImlzcyI6Imlzc3VlciIsImV4cCI6MTYzMjMxMTIzNiwiY29nbml0bzpncm91cHMiOlsiQVBJX1VTRVIiXX0.ImG80nV8Amb9Q4pZrcGm6cYnfSo7onHy_q0E_7u6w8muUOdDQOcmgPpfmdqsu8fhlYGouEhR8y5UWctp-LT-cFuoquVFELY-IeM02zj7hRVqNrSvhATeVUai8xUHcq7cxmf6IiWcrSwcRSN9oA1F6pc3E0q88Kk3nd_ieHMZfGbbbs1DkycqsR-KIGkymVN0W527meBM_9P8PMknfzvYWUJh4GV_h47V0Fx_e9R6lOTH0NfRYWc1nSM3q8cU6RkypF-Uyq9dHkcuiYlwwBYqRenoHbFbLsYFdGUby6OMwERbKlUvdCXyJB_yyp7-QVKRuSetaYCWksJUqp5pdxujXw");
  }

    /** Given a Post call on the endpoint, ensure a 405 error is returned. */
  @Test
  public void postCallOnEndpoint() throws Exception {
    // Arrange
    HttpEntity<String> request = new HttpEntity<String>(null, headers);

    // Act
    ResponseEntity<String> response =
        restTemplate.exchange(endpointUrl, HttpMethod.POST, request, String.class);

    // Assert
    assertTrue(response.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED);
  }

  /** Given a Patch call on the endpoint, ensure a 405 error is returned. */
  @Test
  public void patchCallOnEndpoint() throws Exception {
    // Arrange
    HttpEntity<String> request = new HttpEntity<String>(null, headers);

    // Act
    ResponseEntity<String> response =
        restTemplate.exchange(endpointUrl, HttpMethod.PATCH, request, String.class);

    // Assert
    assertTrue(response.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED);
  }

  /** Given a Put call on the endpoint, ensure a 405 error is returned. */
  @Test
  public void putCallOnEndpoint() throws Exception {
    // Arrange
    HttpEntity<String> request = new HttpEntity<String>(null, headers);

    // Act
    ResponseEntity<String> response =
        restTemplate.exchange(endpointUrl, HttpMethod.PUT, request, String.class);

    // Assert
    assertTrue(response.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED);
  }

  /** Given a Delete call on the endpoint, ensure a 405 error is returned. */
  @Test
  public void deleteCallOnEndpoint() throws Exception {
    // Arrange
    HttpEntity<String> request = new HttpEntity<String>(null, headers);

    // Act
    ResponseEntity<String> response =
        restTemplate.exchange(endpointUrl, HttpMethod.DELETE, request, String.class);

    // Assert
    assertTrue(response.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED);
  }
}

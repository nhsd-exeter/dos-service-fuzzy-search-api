package uk.nhs.digital.uec.api.integration.fuzzysearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.integration.AuthServiceTest;
import uk.nhs.digital.uec.api.util.PropertySourceResolver;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class which passes requests through the Fuzzy Search endpoint and asserts desired API
 * behavior. Only the model layer will be mocked here.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FuzzySearchGeneralErrorsTest extends AuthServiceTest {

  @Autowired private PropertySourceResolver propertySourceResolver;

  @Autowired private TestRestTemplate restTemplate;

  private static String endpointUrl;

  private MultiValueMap<String, String> headers;

  @BeforeEach
  public void configureProperties() throws Exception {
    endpointUrl = propertySourceResolver.endpointUrl;
    headers = getAuthorizedHeader();
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

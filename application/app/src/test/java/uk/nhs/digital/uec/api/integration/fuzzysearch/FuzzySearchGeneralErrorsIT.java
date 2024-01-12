package uk.nhs.digital.uec.api.integration.fuzzysearch;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Test class which passes request import org.springframework.util.MultiValueMap;
 *
 * <p>through the Fuzzy Search endpoint and asserts desired API behavior. Only the model layer will
 * be mocked here.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FuzzySearchGeneralErrorsIT extends AuthServiceIT {

  @Autowired private PropertySourceResolver propertySourceResolver;

  private String endpointUrl;

  MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

  @BeforeEach
  public void configureProperties() throws Exception {
    endpointUrl = propertySourceResolver.endpointUrl;
    headers = getAuthorizedHeader();
  }

  /** Given a Post call on the endpoint, ensure a 405 error is returned. */
  @Test
  public void postCallOnEndpoint() throws Exception {
    // Arrange
    HttpEntity<String> request = new HttpEntity<>(null, headers);

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
    HttpEntity<String> request = new HttpEntity<>(null, headers);

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
    HttpEntity<String> request = new HttpEntity<>(null, headers);

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
    HttpEntity<String> request = new HttpEntity<>(null, headers);

    // Act
    ResponseEntity<String> response =
        restTemplate.exchange(endpointUrl, HttpMethod.DELETE, request, String.class);

    // Assert
    assertTrue(response.getStatusCode() == HttpStatus.METHOD_NOT_ALLOWED);
  }
}

package uk.nhs.digital.uec.api.auth.request;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.auth.factory.SslFactorySupplier;
import uk.nhs.digital.uec.api.auth.util.CookieUtil;

/**
 * Class to encapsulate logic to call endpoints of the fuzzy search from within the application.
 *
 * <p>Specially, the class deals with providing the request call with authentication components so
 * the code calling the endpoint does not have to worry about it.
 */
@Service
public class FuzzySearchRestClient {

  private RestTemplateBuilder restTemplateBuilder;

  /**
   * Fuzzy Searchrequest scoped object to hold request related details such as authentication
   * tokens.
   */
  private FuzzySearchRequest fuzzySearchRequest;

  @Autowired
  public FuzzySearchRestClient(
      final RestTemplateBuilder restTemplateBuilder, final FuzzySearchRequest fuzzySearchRequest) {
    this.restTemplateBuilder = restTemplateBuilder;
    this.fuzzySearchRequest = fuzzySearchRequest;
  }

  /**
   * Sends the request to the specified endpoint with authentication tokens.
   *
   * @param endpointUrl the url to send the request to.
   * @param httpMethod the method in which to invoke the endpoint {@link HttpMethod}.
   * @param body the request body.
   */
  public void sendRequestWithAuthentication(
      final String endpointUrl, final HttpMethod httpMethod, final Map<String, String> body) {
    final HttpHeaders requestHeaders =
        CookieUtil.addAuthTokensToCookieHeader(fuzzySearchRequest.getAuthenticationToken());
    final HttpEntity requestEntity = new HttpEntity(body, requestHeaders);

    restTemplateBuilder
        .requestFactory(new SslFactorySupplier())
        .build()
        .exchange(endpointUrl, httpMethod, requestEntity, ResponseEntity.class);
  }
}

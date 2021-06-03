package uk.nhs.digital.uec.api.auth.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import uk.nhs.digital.uec.api.auth.factory.SslFactorySupplier;
import uk.nhs.digital.uec.api.auth.model.AuthenticationToken;

@ExtendWith(SpringExtension.class)
public class FuzzySearchRestClientTest {

  private static final String testUrl = "test/url";

  private static final String ACCESS_TOKEN = "TEST_ACCESS_TOKEN";

  private static final String REFRESH_TOKEN = "TEST_REFRESH_TOKEN";

  private static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN=" + ACCESS_TOKEN;

  private static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN=" + REFRESH_TOKEN;

  private final Map<String, String> body = new HashMap<>();

  private final AuthenticationToken authenticationToken =
      new AuthenticationToken(ACCESS_TOKEN, REFRESH_TOKEN);

  @Mock private FuzzySearchRequest fuzzySearchRequest;

  @Mock private RestTemplateBuilder restTemplateBuilder;

  @Mock private RestTemplate restTemplate;

  @InjectMocks private FuzzySearchRestClient fuzzySearchRestClient;

  @Captor private ArgumentCaptor<HttpEntity> httpEntityCaptor;

  @Test
  public void sendRequestWithAuthenticationSuccess() {

    when(fuzzySearchRequest.getAuthenticationToken()).thenReturn(authenticationToken);
    when(restTemplateBuilder.requestFactory(any(SslFactorySupplier.class)))
        .thenReturn(restTemplateBuilder);
    when(restTemplateBuilder.build()).thenReturn(restTemplate);

    body.put("Test", "TestValue");
    fuzzySearchRestClient.sendRequestWithAuthentication(testUrl, HttpMethod.POST, body);

    verify(restTemplate, times(1))
        .exchange(eq(testUrl), eq(HttpMethod.POST), httpEntityCaptor.capture(), any(Class.class));

    HttpEntity requestEntity = httpEntityCaptor.getValue();
    Map<String, String> requestBody = (Map<String, String>) requestEntity.getBody();
    assertEquals(body, requestBody);

    List<String> cookies = requestEntity.getHeaders().get("Cookie");
    assertTrue(cookies.contains(ACCESS_TOKEN_COOKIE));
    assertTrue(cookies.contains(REFRESH_TOKEN_COOKIE));
  }
}

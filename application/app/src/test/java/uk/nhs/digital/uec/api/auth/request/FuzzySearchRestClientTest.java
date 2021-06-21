package uk.nhs.digital.uec.api.auth.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
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

  // @Test
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

  @Test
  public void testJWT() throws Exception {
    String token =
        "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJpZCIsImlhdCI6MTYyMzk0MDUzOSwic3ViIjoiYWRtaW5AbmhzLm5ldCIsImlzcyI6Imlzc3VlciIsImV4cCI6MTYyMzk0MDcxOSwiY29nbml0bzpncm91cHMiOlsiQVBJX1VTRVIiXX0.DXdKMkl5ImhsZXvgWYw6w0P9cxnNruFbXm10SB-fKtWHIn_q_H0vNPgs0K0_XUHnmkLTOYMgP5dKsGUGpQjsSaGeycYmMPCdmzngXkUlCagT9xfrsIuGeuj1RydkZOUNnjgszsBIGSDOrk5-VMk8MVtp0QZ1C9XB0B0xEqQkGI8ZTykmuBfRzpM7CJ-k6f07enRUglxJXZLnG-NfRhRTbc6hYbwYZhYcTu0707b_JlLBU_v38ApNy6niXmKRJEbAvO08l13AdQISUPK2oOyn1TkNAqb_BJVVI8z0RRmHnTO36uN6z-r53DzXArFrMu-ZXOmsaFlT_LjnWx24uicZww";
    try {
      DecodedJWT jwt = JWT.decode(token);

      // Headers
      String header = jwt.getHeader();
      System.out.println("HEADER = " + header);

      // claims
      Map<String, Claim> claims = jwt.getClaims();
      claims.forEach((k, v) -> System.out.println("KEY = " + k + ", VALUE =" + v));

      System.out.println("-----------------------------------------------");
      System.out.println("Subject = " + jwt.getSubject());
      System.out.println("Algorithm = " + jwt.getAlgorithm());
      System.out.println("Issuer = " + jwt.getIssuer());
      System.out.println("Payload = " + jwt.getPayload());
      System.out.println("Token = " + jwt.getToken());
      System.out.println("Subject = " + jwt.getContentType());
      System.out.println("Id = " + jwt.getId());
      System.out.println("KeyId = " + jwt.getKeyId());
      System.out.println("Signature = " + jwt.getSignature());
      System.out.println("ExpiresAt = " + jwt.getExpiresAt());
      System.out.println("IssuedAt = " + jwt.getIssuedAt());
      System.out.println("NotBefore = " + jwt.getNotBefore());
      System.out.println("Audience = " + jwt.getAudience());

    } catch (JWTDecodeException e) {
      // Invalid token
      System.out.println(e);
    }
  }
}

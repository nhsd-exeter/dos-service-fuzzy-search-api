package uk.nhs.digital.uec.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.net.ssl.SSLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.model.PostcodeLocation;

@ExtendWith(SpringExtension.class)
public class WebClientUtilTest {

  @InjectMocks private WebClientUtil webClientUtil;
  @Mock private WebClient authWebClient;
  @Mock private WebClient postCodeMappingWebClient;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestBodySpec requestBodySpecMock;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpecMock;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

  @Mock private WebClient.ResponseSpec responseSpecMock;

  private AuthToken authToken;
  private String user;
  private String userPass;
  private MultiValueMap<String, String> headers;

  @BeforeEach
  public void setUp() {
    authToken = new AuthToken();
    authToken.setAccessToken("MOCK-ACCESS-TOKEN");
    authToken.setRefreshToken("MOCK-ACCESS-REFRESH-TOKEN");
    user = "admin@nhs.net";
    userPass = "password";
    headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + authToken.getAccessToken());
  }

  @Test
  public void getHeaderTest() throws SSLException {
    Credential credential = Credential.builder().emailAddress(user).password(userPass).build();
    authWebClient = getMockedAuthWebClient(authToken);
    AuthToken responseAuthToken =
        webClientUtil.getAuthenticationToken(credential, "/authentication/login");
    assertEquals(authToken.getAccessToken(), responseAuthToken.getAccessToken());
  }

  @Test
  public void getPostCodeMappingsTest() throws SSLException, InvalidParameterException {
    List<String> postCodes = new ArrayList<>();
    postCodes.add("EX1 2SR");

    PostcodeLocation postcodeLocation = new PostcodeLocation();
    postcodeLocation.setEasting(123677);
    postcodeLocation.setNorthing(655343);
    postcodeLocation.setPostCode("EX1 2PR");

    postCodeMappingWebClient = getPostCodeWebClient(postcodeLocation);

    List<PostcodeLocation> postcodeMappings =
        webClientUtil.getPostcodeMappings(postCodes, headers, "api/search");
    PostcodeLocation returnedLocation = postcodeMappings.get(0);
    assertEquals(655343, returnedLocation.getNorthing());
    assertEquals(123677, returnedLocation.getEasting());
    assertEquals("EX1 2PR", returnedLocation.getPostCode());
  }

  private WebClient getMockedAuthWebClient(final AuthToken resp) {
    when(authWebClient.post()).thenReturn(requestBodyUriSpecMock);
    when(requestBodyUriSpecMock.uri(any(Function.class))).thenReturn(requestBodySpecMock);
    when(requestBodySpecMock.header(any(), any())).thenReturn(requestBodySpecMock);
    when(requestHeadersSpecMock.header(any(), any())).thenReturn(requestHeadersSpecMock);
    when(requestBodySpecMock.accept(any())).thenReturn(requestBodySpecMock);
    when(requestBodySpecMock.body(any())).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(AuthToken.class)).thenReturn(Mono.just(resp));
    return authWebClient;
  }

  private WebClient getPostCodeWebClient(final PostcodeLocation resp) {
    when(postCodeMappingWebClient.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.headers(any(Consumer.class))).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToFlux(PostcodeLocation.class)).thenReturn(Flux.just(resp));
    return postCodeMappingWebClient;
  }

  @Test
  public void getMockedAuthWebClientExceptionTest() {
    when(authWebClient.post()).thenThrow(RuntimeException.class);
    Credential credential = Credential.builder().emailAddress(user).password(userPass).build();
    AuthToken responseAuthToken =
        webClientUtil.getAuthenticationToken(credential, "/authentication/login");
    assertNull(responseAuthToken);
  }

  @Test
  public void getPostCodeMappingsStatus400ExceptionTest() throws InvalidParameterException {
    String body =
        "{\r\n"
            + "    \"validationCode\": \"VAL-002\",\r\n"
            + "    \"message\": \"Postcode is provided but it is invalid\"\r\n"
            + "}";
    byte[] b = body.getBytes(StandardCharsets.UTF_8);
    WebClientResponseException clientResponseException =
        new WebClientResponseException(400, null, null, b, null, null);
    when(postCodeMappingWebClient.get()).thenThrow(clientResponseException);
    List<String> postCodes = new ArrayList<>();
    postCodes.add("EX1 3SR");
    assertThrows(
        InvalidParameterException.class,
        () -> webClientUtil.getPostcodeMappings(postCodes, headers, "api/search"));
  }

  @Test
  public void getPostCodeMappingsNon400ExceptionTest() throws InvalidParameterException {
    String body =
        "{\r\n"
            + "    \"validationCode\": \"VAL-001\",\r\n"
            + "    \"message\": \"No services found for the given name or postcode\"\r\n"
            + "}";
    byte[] b = body.getBytes(StandardCharsets.UTF_8);
    WebClientResponseException clientResponseException =
        new WebClientResponseException(404, null, null, b, null, null);
    when(postCodeMappingWebClient.get()).thenThrow(clientResponseException);
    List<String> postCodes = new ArrayList<>();
    postCodes.add("EX1 3SR");

    List<PostcodeLocation> postcodeMappings =
        webClientUtil.getPostcodeMappings(postCodes, headers, "api/search");
    assertTrue(postcodeMappings.isEmpty());
  }
}

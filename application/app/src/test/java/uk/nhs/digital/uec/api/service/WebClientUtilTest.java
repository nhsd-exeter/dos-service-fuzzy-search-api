package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.net.ssl.SSLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.util.WebClientUtil;

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
  private String loginUri = "/authentication/login";

  @Test
  public void getHeaderTest() throws SSLException {
    List<String> postCodes = new ArrayList<>();
    postCodes.add("EX1 1SR");
    AuthToken authToken = new AuthToken();
    authToken.setAccessToken("MOCK-ACCESS-TOKEN");
    authToken.setRefreshToken("MOCK-ACCESS-REFRESH-TOKEN");
    Credential credential =
        Credential.builder().emailAddress("admin@nhs.net").password("password").build();
    authWebClient = getMockedAuthWebClient(authToken);
    AuthToken authenticationToken = webClientUtil.getAuthenticationToken(credential, loginUri);
    assertEquals("MOCK-ACCESS-TOKEN", authenticationToken.getAccessToken());
  }

  @Test
  public void getPostCodeMappingsTest() throws SSLException {
    List<String> postCodes = new ArrayList<>();
    postCodes.add("EX1 1SR");
    AuthToken authToken = new AuthToken();
    authToken.setAccessToken("MOCK-ACCESS-TOKEN");
    authToken.setRefreshToken("MOCK-ACCESS-REFRESH-TOKEN");

    PostcodeLocation postcodeLocation = new PostcodeLocation();
    postcodeLocation.setEasting(123677);
    postcodeLocation.setNorthing(655343);
    postcodeLocation.setPostCode("EX1 1PR");

    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + authToken.getAccessToken());

    postCodeMappingWebClient = getPostCodeWebClient(postcodeLocation);

    List<PostcodeLocation> postcodeMappings =
        webClientUtil.getPostcodeMappings(postCodes, headers, "api/search");
    PostcodeLocation returnedLocation = postcodeMappings.get(0);
    assertEquals(655343, returnedLocation.getNorthing());
    assertEquals(123677, returnedLocation.getEasting());
    assertEquals("EX1 1PR", returnedLocation.getPostCode());
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
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer Access-Token");
    when(postCodeMappingWebClient.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.headers(any(Consumer.class))).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToFlux(PostcodeLocation.class)).thenReturn(Flux.just(resp));
    return postCodeMappingWebClient;
  }
}

package uk.nhs.digital.uec.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import uk.nhs.digital.uec.api.model.google.GeoLocationResponse;
import uk.nhs.digital.uec.api.model.google.GeoLocationResponseResult;
import uk.nhs.digital.uec.api.model.google.Geometry;
import uk.nhs.digital.uec.api.model.google.Location;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesResponse;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;

import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class WebClientUtilTest {

  @InjectMocks
  private WebClientUtil webClientUtil;
  @Mock
  private WebClient authWebClient;
  @Mock
  private WebClient postCodeMappingWebClient;
  @Mock
  private WebClient googleApiWebClient;
  @Mock
  private ObjectMapper mapper;
  @Mock
  private WebClient nhsChoicesApiWebClient;


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

  @Mock
  private WebClient.ResponseSpec responseSpecMock;

  private AuthToken authToken;
  private String user;
  private String userPass;
  private MultiValueMap<String, String> headers;
  private static final String URI = "api/search";
  private static final String AUTH_URI = "/authentication/login";
  private NHSChoicesResponse nhsChoicesResponse;

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
    nhsChoicesResponse = new NHSChoicesResponse();
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testConstructor() {
    assertNotNull(webClientUtil.getAuthWebClient());
    assertNotNull(webClientUtil.getPostCodeMappingWebClient());
    assertNotNull(webClientUtil.getGoogleApiWebClient());
    assertNotNull(webClientUtil.getNhsChoicesApiWebClient());
    assertNotNull(webClientUtil.getObjectMapper());
  }


  @Test
  public void retrieveNHSChoicesServices() throws ExecutionException, InterruptedException, JsonProcessingException {
    //Given
    String searchTerms = "Search";
    String searchLatitude = "0.0";
    String searchLongitude = "0.0";

    webClientUtil.setNhsChoicesApiWebClient(getMockedNHSChoicesClient(nhsChoicesResponse));

    // Act
    CompletableFuture<List<NHSChoicesV2DataModel>> result = webClientUtil.retrieveNHSChoicesServices(searchLatitude, searchLongitude, searchTerms);

    // Assert
    assertNotNull(result);
    assertTrue(result.isDone());
    List<NHSChoicesV2DataModel> resultList = result.get();
    assertNotNull(resultList);
  }

  @Test
  public void getHeaderTest() throws SSLException {
    Credential credential = Credential.builder().emailAddress(user).password(userPass).build();
    webClientUtil.setAuthWebClient(getMockedAuthWebClient(authToken));
    AuthToken responseAuthToken = webClientUtil.getAuthenticationToken(credential, AUTH_URI);
    assertEquals(authToken.getAccessToken(), responseAuthToken.getAccessToken());
  }

  @Test
  public void getPostCodeMappingsTest() throws SSLException, InvalidParameterException {
    List<String> postCodes = new ArrayList<>();
    postCodes.add("EX1 2SR");

    PostcodeLocation postcodeLocation = new PostcodeLocation();
    postcodeLocation.setEasting(123677);
    postcodeLocation.setNorthing(655343);
    postcodeLocation.setPostcode("EX1 2PR");

    webClientUtil.setPostCodeMappingWebClient(getPostCodeWebClient(postcodeLocation));

    List<PostcodeLocation> postcodeMappings =
      webClientUtil.getPostcodeMappings(postCodes, headers, URI);
    PostcodeLocation returnedLocation = postcodeMappings.get(0);
    assertEquals(655343, returnedLocation.getNorthing());
    assertEquals(123677, returnedLocation.getEasting());
    assertEquals("EX1 2PR", returnedLocation.getPostcode());
  }

  @Test
  public void getGoogleAPIMappingsTest() throws SSLException, InvalidParameterException {

    Geometry geometry = mock(Geometry.class);
    Location location = mock(Location.class);
    when(location.getLat()).thenReturn(22.0);
    when(location.getLng()).thenReturn(22.0);
    when(geometry.getLocation()).thenReturn(location);

    GeoLocationResponse mockGeoLocationResponse = mock(GeoLocationResponse.class);
    GeoLocationResponseResult[] geoLocationResponseResults = new GeoLocationResponseResult[1];
    GeoLocationResponseResult geoLocationResponseResult = mock(GeoLocationResponseResult.class);
    when(geoLocationResponseResult.getGeometry()).thenReturn(geometry);
    geoLocationResponseResults[0] = geoLocationResponseResult;
    when(mockGeoLocationResponse.getGeoLocationResponseResults()).thenReturn(geoLocationResponseResults);

    when(googleApiWebClient.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);

    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(GeoLocationResponse.class)).thenReturn(Mono.just(mockGeoLocationResponse));

    webClientUtil.setGoogleApiWebClient(googleApiWebClient);

    GeoLocationResponse geoLocationResponse =
      webClientUtil.getGeoLocation("mk13 0LG", "XXXXXXXXX", "/api/goe/json");

    GeoLocationResponseResult geoLocationResponseResult2 = geoLocationResponse.getGeoLocationResponseResults()[0];
    assertEquals(geometry, geoLocationResponseResult2.getGeometry());
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

  private WebClient getMockedNHSChoicesClient(final NHSChoicesResponse response) throws JsonProcessingException {
    when(nhsChoicesApiWebClient.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.headers(any(Consumer.class))).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just(response.toString()));
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
    when(mapper.readValue(anyString(), any(TypeReference.class)))
      .thenReturn(new HashMap<String, Object>() {{
        List<Map<String, Object>> modelMaps = new ArrayList<>();
        Map<String, Object> modelMap = new HashMap<>();
        //TODO- Build out the NHSChoices response
        modelMap.put("organisationName", "exampleName");

        modelMaps.add(modelMap);
        put("value", modelMaps);
        put("@data.nextLink", "nextLink");
      }});
    return nhsChoicesApiWebClient;
  }

  @Test
  public void getMockedAuthWebClientExceptionTest() {
    when(authWebClient.post()).thenThrow(RuntimeException.class);
    Credential credential = Credential.builder().emailAddress(user).password(userPass).build();
    AuthToken responseAuthToken = webClientUtil.getAuthenticationToken(credential, AUTH_URI);
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
    webClientUtil.setPostCodeMappingWebClient(getPostCodeWebClient(mock(PostcodeLocation.class)));
    when(postCodeMappingWebClient.get()).thenThrow(clientResponseException);
    List<String> postCodes = new ArrayList<>();
    postCodes.add("EX1 3SR");
    assertThrows(
      InvalidParameterException.class,
      () -> webClientUtil.getPostcodeMappings(postCodes, headers, URI));
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
      webClientUtil.getPostcodeMappings(postCodes, headers, URI);
    assertTrue(postcodeMappings.isEmpty());
  }

  @Test
  public void getGeoLocationExceptionHandlingTest() throws SSLException {
    when(googleApiWebClient.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(GeoLocationResponse.class)).thenThrow(WebClientResponseException.class);

    webClientUtil.setGoogleApiWebClient(googleApiWebClient);

    assertThrows(WebClientResponseException.class, () -> {
      webClientUtil.getGeoLocation("mk13 0LG", "XXXXXXXXX", "/api/goe/json");
    });
  }

  @Test
  public void getAuthenticationToken_NullOrException() throws SSLException {
    when(authWebClient.post()).thenReturn(requestBodyUriSpecMock);
    when(requestBodyUriSpecMock.uri(any(Function.class))).thenReturn(requestBodySpecMock);
    when(requestBodySpecMock.header(any(), any())).thenReturn(requestBodySpecMock);
    when(requestHeadersSpecMock.header(any(), any())).thenReturn(requestHeadersSpecMock);
    when(requestBodySpecMock.accept(any())).thenReturn(requestBodySpecMock);
    when(requestBodySpecMock.body(any())).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(AuthToken.class)).thenReturn(Mono.empty()); // Simulate null response

    webClientUtil.setAuthWebClient(authWebClient);

    AuthToken responseAuthToken = webClientUtil.getAuthenticationToken(new Credential(), "/authentication/login");

    assertNull(responseAuthToken);
  }

  @Test
  public void retrieveNHSChoicesServices_HttpErrorStatus() throws ExecutionException, InterruptedException {
    String searchTerms = "Search";
    String searchLatitude = "0.0";
    String searchLongitude = "0.0";
    webClientUtil.setNhsChoicesApiWebClient(getMockedNHSChoicesClientWithError(HttpStatus.INTERNAL_SERVER_ERROR));

    CompletableFuture<List<NHSChoicesV2DataModel>> result = webClientUtil.retrieveNHSChoicesServices(searchLatitude, searchLongitude, searchTerms);

    assertNotNull(result);
    assertTrue(result.isDone());
    assertThrows(ExecutionException.class, result::get);
  }

  @Test
  public void getGeoLocation_OnErrorResume() throws SSLException {
    when(googleApiWebClient.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(GeoLocationResponse.class)).thenThrow(WebClientResponseException.class);

    webClientUtil.setGoogleApiWebClient(googleApiWebClient);

    assertThrows(WebClientResponseException.class, () -> webClientUtil.getGeoLocation("mk13 0LG", "XXXXXXXXX", "/api/goe/json"));
  }

  @Test
  public void handleWebClientResponseException_Status400() throws InvalidParameterException {
    WebClientResponseException exception = new WebClientResponseException(HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null, null);

    assertThrows(InvalidParameterException.class, () -> webClientUtil.handleWebClientResponseException(exception));
  }

  @Test
  public void handleWebClientResponseException_Status404() throws InvalidParameterException {
    WebClientResponseException exception = new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null, null);

    webClientUtil.handleWebClientResponseException(exception);
  }

  private WebClient getMockedNHSChoicesClientWithError(HttpStatus status) {
    when(nhsChoicesApiWebClient.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.headers(any(Consumer.class))).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just("Mocked Response"));
    when(responseSpecMock.toEntity(String.class)).thenReturn(Mono.just(ResponseEntity.status(status).body("Mocked Response"))); // Added line
    return nhsChoicesApiWebClient;
  }
}

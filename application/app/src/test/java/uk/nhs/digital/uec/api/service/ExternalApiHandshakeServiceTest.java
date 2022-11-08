package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.service.impl.ExternalApiHandshakeService;
import uk.nhs.digital.uec.api.util.WebClientUtil;

@ExtendWith(SpringExtension.class)
public class ExternalApiHandshakeServiceTest {

  @InjectMocks private ExternalApiHandshakeService externalApiHandshakeService;
  @Mock private WebClientUtil webClientUtilMock;
  @Mock Environment environment;

  private AuthToken authToken;
  private List<PostcodeLocation> postcodeLocations = new ArrayList<>();
  private MultiValueMap<String, String> headers;
  private List<String> postCodes;
  private String profileLocal = "local";
  private String profileMockAuth = "mock_auth";
  private static final String LOCAL_PROFILE = "profileLocal";
  private static final String MOCK_AUTH_PROFILE = "profileMockAuth";
  private static final String MOCK_ACCESS_TOKEN = "Bearer MOCK_POSTCODE_API_ACCESS_TOKEN";
  private static final String AUTHORIZATION = "Authorization";

  @BeforeEach
  public void setup() {

    authToken = new AuthToken();
    authToken.setAccessToken("MOCK-ACCESS-TOKEN");
    authToken.setRefreshToken("MOCK-ACCESS-REFRESH-TOKEN");

    PostcodeLocation postcodeLocation = new PostcodeLocation();
    postcodeLocation.setEasting(123677);
    postcodeLocation.setNorthing(655343);
    postcodeLocation.setPostcode("EX1 1PR");
    postcodeLocations.add(postcodeLocation);

    postCodes = new ArrayList<>();
    postCodes.add("EX2 1SR");

    headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + authToken.getAccessToken());
    String[] stringList = {"local", "mock_auth"};
    when(this.environment.getActiveProfiles()).thenReturn(stringList);
  }

  @Test
  public void getHeaderTestForNonDevProfiles() {
    when(webClientUtilMock.getAuthenticationToken(any(), any())).thenReturn(authToken);
    MultiValueMap<String, String> accessTokenHeader =
        externalApiHandshakeService.getAccessTokenHeader();
    List<String> list = accessTokenHeader.get(AUTHORIZATION);
    assertEquals("Bearer MOCK-ACCESS-TOKEN", list.get(0));
  }

  @Test
  public void getHeaderForDevLocalProfile() {
    ReflectionTestUtils.setField(externalApiHandshakeService, LOCAL_PROFILE, profileLocal);
    MultiValueMap<String, String> accessTokenHeader =
        externalApiHandshakeService.getAccessTokenHeader();
    List<String> list = accessTokenHeader.get(AUTHORIZATION);
    assertEquals(MOCK_ACCESS_TOKEN, list.get(0));
  }

  @Test
  public void getHeaderForDevMockAuthProfile() {
    ReflectionTestUtils.setField(externalApiHandshakeService, MOCK_AUTH_PROFILE, profileMockAuth);
    MultiValueMap<String, String> accessTokenHeader =
        externalApiHandshakeService.getAccessTokenHeader();
    List<String> list = accessTokenHeader.get(AUTHORIZATION);
    assertEquals(MOCK_ACCESS_TOKEN, list.get(0));
  }

  @Test
  public void testIsMockAuthenticationForLocalProfile() {
    ReflectionTestUtils.setField(externalApiHandshakeService, LOCAL_PROFILE, profileLocal);
    boolean mockAuthenticationForProfile =
        externalApiHandshakeService.isMockAuthenticationForProfile();
    assertEquals(true, mockAuthenticationForProfile);
  }

  @Test
  public void testIsMockAuthenticationForMockAuthProfile() {
    ReflectionTestUtils.setField(externalApiHandshakeService, MOCK_AUTH_PROFILE, profileMockAuth);
    boolean mockAuthenticationForProfile =
        externalApiHandshakeService.isMockAuthenticationForProfile();
    assertEquals(true, mockAuthenticationForProfile);
  }

  @Test
  public void testIsMockAuthenticationForDevProfile() {
    profileLocal = "xyz";
    ReflectionTestUtils.setField(externalApiHandshakeService, LOCAL_PROFILE, profileLocal);
    boolean mockAuthenticationForProfile =
        externalApiHandshakeService.isMockAuthenticationForProfile();
    assertEquals(false, mockAuthenticationForProfile);
  }

  @Test
  public void getPostcodeMappingsTest() throws InvalidParameterException {
    when(webClientUtilMock.getPostcodeMappings(any(), any(), any())).thenReturn(postcodeLocations);

    List<PostcodeLocation> postcodeMappings =
        externalApiHandshakeService.getPostcodeMappings(postCodes, headers);
    PostcodeLocation postcodeLocation = postcodeMappings.get(0);

    assertEquals(123677, postcodeLocation.getEasting());
    assertEquals(655343, postcodeLocation.getNorthing());
    assertEquals("EX1 1PR", postcodeLocation.getPostcode());
  }
}

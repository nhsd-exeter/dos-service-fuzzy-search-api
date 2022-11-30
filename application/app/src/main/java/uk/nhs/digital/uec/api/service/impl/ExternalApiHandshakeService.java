package uk.nhs.digital.uec.api.service.impl;

import static uk.nhs.digital.uec.api.authentication.constants.MockAuthenticationConstants.MOCK_PCA_ACCESS_TOKEN;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.model.google.GeoLocationResponse;
import uk.nhs.digital.uec.api.service.ExternalApiHandshakeInterface;
import uk.nhs.digital.uec.api.util.WebClientUtil;

@Service
@Slf4j
public class ExternalApiHandshakeService implements ExternalApiHandshakeInterface {

  @Value("${postcode.mapping.uri}")
  private String psmUri;

  @Value("${auth.login.uri}")
  private String loginUri;

  @Value("${postcode.mapping.user}")
  private String postcodeMappingUser;

  @Value("${postcode.mapping.password}")
  private String postcodeMappingPassword;

  @Value("${profile.local}")
  private String profileLocal;

  @Value("${profile.mock_auth}")
  private String profileMockAuth;

  @Value("${google.api.uri}")
  private String googleApiUri;

  @Value("${google.api.key}")
  private String googleApiKey;

  @Autowired private WebClientUtil webClientUtil;

  @Autowired private Environment environment;

  public List<PostcodeLocation> getPostcodeMappings(
      List<String> postCodes, MultiValueMap<String, String> headers)
      throws InvalidParameterException {
    return webClientUtil.getPostcodeMappings(postCodes, headers, psmUri);
  }

  @Override
  public MultiValueMap<String, String> getAccessTokenHeader() {
    AuthToken authToken = null;
    Credential credential =
        Credential.builder()
            .emailAddress(postcodeMappingUser)
            .password(postcodeMappingPassword)
            .build();
    log.info("Attempting to log in with user: {}",postcodeMappingUser);
    log.debug("Attempting to log in with user: {}:{}",postcodeMappingUser,postcodeMappingPassword);
    if (isMockAuthenticationForProfile()) {
      authToken = new AuthToken();
      authToken.setAccessToken(MOCK_PCA_ACCESS_TOKEN);
    } else {
      authToken = webClientUtil.getAuthenticationToken(credential, loginUri);
    }
    log.info("Login complete");
    return createAuthenticationHeader(authToken);
  }

  public boolean isMockAuthenticationForProfile() {
    if (environment == null) return true;
    return Arrays.stream(environment.getActiveProfiles())
        .filter(Objects::nonNull)
        .anyMatch(
            env -> (env.equalsIgnoreCase(profileMockAuth) || env.equalsIgnoreCase(profileLocal)));
  }

  private MultiValueMap<String, String> createAuthenticationHeader(AuthToken authToken) {
    String token = Objects.nonNull(authToken) ? authToken.getAccessToken() : null;
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + token);
    return headers;
  }

  @Override
  public GeoLocationResponse getGeoCoordinates(String address) throws InvalidParameterException {
    return webClientUtil.getGeoLocation(address, googleApiKey, googleApiUri);
  }
}

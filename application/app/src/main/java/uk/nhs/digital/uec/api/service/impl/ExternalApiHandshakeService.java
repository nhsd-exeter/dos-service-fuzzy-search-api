package uk.nhs.digital.uec.api.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.service.ExternalApiHandshakeInterface;
import uk.nhs.digital.uec.api.util.WebClientUtil;

@Service
public class ExternalApiHandshakeService implements ExternalApiHandshakeInterface {

  @Value("${postcode.mapping.uri}")
  private String psmUri;

  @Value("${auth.login.uri}")
  private String loginUri;

  @Value("${postcode.mapping.user}")
  private String postcodeMappingUser;

  @Value("${postcode.mapping.password}")
  private String postcodeMappingPassword;

  @Autowired private WebClientUtil webClientUtil;

  public List<PostcodeLocation> getPostcodeMappings(
      List<String> postCodes, MultiValueMap<String, String> headers) {
    return webClientUtil.getPostcodeMappings(postCodes, headers, psmUri);
  }

  @Override
  public MultiValueMap<String, String> getAccessTokenHeader() {
    Credential credential =
        Credential.builder()
            .emailAddress(postcodeMappingUser)
            .password(postcodeMappingPassword)
            .build();
    AuthToken authToken = webClientUtil.getAuthenticationToken(credential, loginUri);
    return authToken != null ? createAuthenticationHeader(authToken) : null;
  }

  private MultiValueMap<String, String> createAuthenticationHeader(AuthToken authToken) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + authToken.getAccessToken());
    return headers;
  }
}

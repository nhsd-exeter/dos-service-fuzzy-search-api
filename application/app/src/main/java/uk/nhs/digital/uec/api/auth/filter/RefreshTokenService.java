package uk.nhs.digital.uec.api.auth.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestTemplate;
import uk.nhs.digital.uec.api.auth.model.AuthTokens;
import uk.nhs.digital.uec.api.auth.model.RefreshTokens;
import uk.nhs.digital.uec.api.auth.util.CheckArgument;

/** Responsible for retrieving fresh tokens from the user-management service. */
@Slf4j
public class RefreshTokenService {

  protected static final String REFRESH_PATH = "/api/login/refresh";

  private RestTemplate restTemplate;

  public RefreshTokenService(RestTemplate restTemplate) {
    CheckArgument.isNotNull(restTemplate, "restTemplate must not be null");
    this.restTemplate = restTemplate;
  }

  /**
   * Obtain a fresh access token using a refresh token.
   *
   * @param refreshToken The refresh token to be used in the login flow, must not be null
   * @param identityProviderId the identity provider id of the user, must not be null
   * @return {@link AuthTokens} storing the access and the refresh tokens
   * @throws IllegalStateException if {@link AuthTokens} is null or access or refresh tokens are
   *     blank
   */
  public RefreshTokens refresh(String refreshToken, String identityProviderId) {
    CheckArgument.hasText(refreshToken, "refreshToken must have text");
    CheckArgument.hasText(identityProviderId, "identityProviderId must have text");
    RefreshTokens refreshTokens = new RefreshTokens(refreshToken, identityProviderId);
    if (StringUtils.isBlank(refreshTokens.getRefreshToken())) {
      throw new IllegalStateException("Unexpected state: null detected on refreshToken");
    }
    return refreshTokens;
  }
}

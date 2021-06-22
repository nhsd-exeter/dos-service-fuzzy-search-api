package uk.nhs.digital.uec.api.auth.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.uec.api.auth.cognito.CognitoService;
import uk.nhs.digital.uec.api.auth.model.AuthTokens;
import uk.nhs.digital.uec.api.auth.util.CheckArgument;
import uk.nhs.digital.uec.api.service.AccessTokenServiceInterface;

/**
 * Responsible for retrieving fresh tokens from the u import
 * uk.nhs.digital.uec.api.service.AccessTokenServiceInterface;ser-management service.
 */
@Slf4j
public class RefreshTokenService {

  protected static final String REFRESH_PATH = "/dosapi/refreshtoken";
  /**
   * Obtain a fresh access token using a refresh token.
   *
   * @param refreshToken The refresh token to be used in the login flow, must not be null
   * @param identityProviderId the identity provider id of the user, must not be null
   * @param cognitoService since this method is used in Spring filter class, the bean is retreived
   * @return {@link AuthTokens} storing the access and the refresh tokens
   * @throws IllegalStateException if {@link AuthTokens} is null or access or refresh tokens are
   *     blank
   */
  public AuthTokens refresh(
      String refreshToken, String identityProviderId, CognitoService cognitoService) {
    CheckArgument.hasText(refreshToken, "refreshToken must have text");
    AuthTokens authTokens =
        cognitoService.authenticateWithRefreshToken(refreshToken, identityProviderId);
    if (authTokens == null
        || StringUtils.isBlank(authTokens.getAccessToken())
        || StringUtils.isBlank(authTokens.getRefreshToken())) {
      throw new IllegalStateException(
          "Unexpected state: null detected on loginResult / accessToken / refreshToken");
    }
    return authTokens;
  }

  /**
   * Obtain a fresh access token using a refresh token.
   *
   * @param refreshToken The refresh token to be used in the login flow, must not be null
   * @param identityProviderId the identity provider id of the user, must not be null
   * @param accessTokenService since this method is used in Spring filter class, AccessTokenService
   *     Bean is passed as argument by retreiving
   * @return {@link AuthTokens} storing the access and the refresh tokens
   * @throws IllegalStateException if {@link AuthTokens} is null or access or refresh tokens are
   *     blank
   */
  public AuthTokens refresh(
      String refreshToken,
      String identityProviderId,
      AccessTokenServiceInterface accessTokenService) {
    CheckArgument.hasText(refreshToken, "refreshToken must have text");
    AuthTokens authTokens = accessTokenService.refreshToken(refreshToken, identityProviderId);
    if (authTokens == null
        || StringUtils.isBlank(authTokens.getAccessToken())
        || StringUtils.isBlank(authTokens.getRefreshToken())) {
      throw new IllegalStateException(
          "Unexpected state: null detected on loginResult / accessToken / refreshToken");
    }
    return authTokens;
  }
}

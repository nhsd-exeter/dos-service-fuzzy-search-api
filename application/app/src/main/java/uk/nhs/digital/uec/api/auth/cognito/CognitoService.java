package uk.nhs.digital.uec.api.auth.cognito;

import uk.nhs.digital.uec.api.auth.exception.InvalidAccessTokenException;
import uk.nhs.digital.uec.api.auth.model.AuthTokens;
import uk.nhs.digital.uec.api.auth.model.Credentials;

public interface CognitoService {

  /**
   * Authenticate a user
   *
   * @param credentials the credentials supplied by the user, must not be null
   * @return {@link LoginResult}, will never be null
   * @throws InvalidAccessTokenException if invalid password or unauthorized Cognito login is
   *     detected
   */
  AuthTokens authenticate(Credentials credentials) throws InvalidAccessTokenException;

  /**
   * Authenticate a user using the refresh token and their identity provider id
   *
   * @param refreshToken the refresh token, must have text
   * @param identityProviderId the identity provider id of the user, must have text
   * @return {@link LoginResult}, will never be null
   */
  AuthTokens authenticateWithRefreshToken(String refreshToken, String identityProviderId);
}

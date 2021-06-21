package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.auth.exception.NoRolesException;
import uk.nhs.digital.uec.api.auth.model.AuthTokens;
import uk.nhs.digital.uec.api.auth.model.Credentials;

public interface AccessTokenServiceInterface {

  public AuthTokens accessToken(Credentials credentials) throws NoRolesException;

  public AuthTokens refreshToken(String refreshToken, String identityProviderId);
}

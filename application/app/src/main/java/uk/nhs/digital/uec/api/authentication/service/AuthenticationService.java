package uk.nhs.digital.uec.api.authentication.service;

import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

public interface AuthenticationService {

  AuthToken getAccessToken(Credential credentials) throws UnauthorisedException;

  AuthToken refreshToken(String refreshToken, String identityProviderId);
}

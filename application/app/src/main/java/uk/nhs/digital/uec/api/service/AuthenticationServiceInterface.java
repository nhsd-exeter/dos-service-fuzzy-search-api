package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

public interface AuthenticationServiceInterface {

  public AuthToken getAccessToken(Credential credentials) throws UnauthorisedException;
}

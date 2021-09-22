package uk.nhs.digital.uec.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;
import uk.nhs.digital.uec.api.service.AuthenticationService;
import uk.nhs.digital.uec.api.service.CognitoIdpService;

@Service
public class AuthenticationServiceImplementation implements AuthenticationService {

  @Autowired private CognitoIdpService cognitoIdpService;

  @Override
  public AuthToken getAccessToken(Credential credentials) throws UnauthorisedException {
    return cognitoIdpService.authenticate(credentials);
  }
}

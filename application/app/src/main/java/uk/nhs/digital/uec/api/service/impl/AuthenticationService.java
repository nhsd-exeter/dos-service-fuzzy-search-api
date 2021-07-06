package uk.nhs.digital.uec.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.authentication.cognito.CognitoIdpService;
import uk.nhs.digital.uec.api.authentication.exception.InvalidCredentialsException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.service.AuthenticationServiceInterface;

@Service
@Slf4j
public class AuthenticationService implements AuthenticationServiceInterface {

  @Autowired private CognitoIdpService cognitoIdpService;

  @Override
  public AuthToken getAccessToken(Credential credentials) throws InvalidCredentialsException {
    return cognitoIdpService.authenticate(credentials);
  }
}

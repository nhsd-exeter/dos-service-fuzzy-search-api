package uk.nhs.digital.uec.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.authentication.cognito.CognitoIdpService;
import uk.nhs.digital.uec.api.authentication.exception.InvalidAccessTokenException;
import uk.nhs.digital.uec.api.authentication.exception.RolesNotFoundException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.service.AuthenticationServiceInterface;

@Service
@Slf4j
public class AuthenticationService implements AuthenticationServiceInterface {

  @Autowired private CognitoIdpService cognitoIdpService;

  @Override
  public AuthToken getAccessToken(Credential credentials) throws RolesNotFoundException {
    String emailAddress = credentials.getEmailAddress();
    try {
      return cognitoIdpService.authenticate(credentials);
    } catch (InvalidAccessTokenException e) {
      log.error("Invalid credentials for [%s]", emailAddress);
      throw new InvalidAccessTokenException(
          String.format("Invalid credentials for [%s]", emailAddress));
    }
  }
}

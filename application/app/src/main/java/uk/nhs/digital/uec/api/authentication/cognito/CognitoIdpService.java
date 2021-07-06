package uk.nhs.digital.uec.api.authentication.cognito;

import uk.nhs.digital.uec.api.authentication.exception.InvalidCredentialsException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

public interface CognitoIdpService {

  /**
   * This method implementation authenticate the user credentials connecting Cognito
   *
   * @param credential
   * @return AuthToken object
   */
  AuthToken authenticate(Credential credential) throws InvalidCredentialsException;
}

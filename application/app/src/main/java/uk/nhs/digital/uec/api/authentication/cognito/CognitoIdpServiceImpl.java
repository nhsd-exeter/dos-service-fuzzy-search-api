package uk.nhs.digital.uec.api.authentication.cognito;

import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.PASSWORD;
import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.USERNAME;
import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.USER_PASSWORD_AUTH;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.authentication.exception.InvalidCredentialsException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

@Service
@Slf4j
public class CognitoIdpServiceImpl implements CognitoIdpService {

  @Autowired private AWSCognitoIdentityProvider cognitoClient;

  @Value("${cognito.userPool.clientId}")
  private String userPoolClientId;

  @Override
  public AuthToken authenticate(Credential credential) throws InvalidCredentialsException {

    Map<String, String> authenticationParameters =
        Map.of(USERNAME, credential.getEmailAddress(), PASSWORD, credential.getPassword());
    try {
      return getAuthenticationTokens(USER_PASSWORD_AUTH, authenticationParameters);
    } catch (InvalidPasswordException | NotAuthorizedException e) {
      log.error(e.getErrorMessage());
      throw new InvalidCredentialsException(e.getErrorMessage());
    } catch (AWSCognitoIdentityProviderException e) {
      log.error(e.getErrorMessage());
      throw new InvalidCredentialsException(e.getErrorMessage());
    }
  }

  private AuthToken getAuthenticationTokens(
      String authFlowType, Map<String, String> authenticationParameters) {
    InitiateAuthRequest authenticationRequest =
        new InitiateAuthRequest()
            .withAuthFlow(authFlowType)
            .withClientId(userPoolClientId)
            .withAuthParameters(authenticationParameters);
    InitiateAuthResult authenticationResult = null;
    authenticationResult = cognitoClient.initiateAuth(authenticationRequest);
    return new AuthToken(
        authenticationResult.getAuthenticationResult().getAccessToken(),
        authenticationResult.getAuthenticationResult().getRefreshToken());
  }
}

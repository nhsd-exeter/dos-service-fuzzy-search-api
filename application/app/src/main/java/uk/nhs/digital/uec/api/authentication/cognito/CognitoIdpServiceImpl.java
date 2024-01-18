package uk.nhs.digital.uec.api.authentication.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

import java.util.HashMap;
import java.util.Map;

import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.*;
import static uk.nhs.digital.uec.api.util.Utils.calculateSecretHash;

@Service
@Slf4j
public class CognitoIdpServiceImpl implements CognitoIdpService {

  private final AWSCognitoIdentityProvider cognitoClient;
  private final Environment environment;

  @Value("${cognito.userPool.clientId}")
  private String userPoolClientId;

  @Value(value = "${cognito.userPool.clientSecret}")
  private String userPoolClientSecret;

  @Autowired
  public CognitoIdpServiceImpl(
    AWSCognitoIdentityProvider cognitoClient,
    Environment environment
  ){
    this.cognitoClient = cognitoClient;
    this.environment = environment;
  }

  @Override
  public AuthToken authenticate(Credential credential) throws UnauthorisedException {
    Map<String, String> authenticationParameters = new HashMap<>();
    authenticationParameters.put(USERNAME, credential.getEmailAddress());
    authenticationParameters.put(PASSWORD, credential.getPassword());
    authenticationParameters.put(
        SECRET_HASH,
        calculateSecretHash(credential.getEmailAddress(), userPoolClientId, userPoolClientSecret));

    try {
      return getAuthenticationTokens(USER_PASSWORD_AUTH, authenticationParameters);
    } catch (AWSCognitoIdentityProviderException e) {
      log.error(e.getErrorMessage());
      throw new UnauthorisedException(e.getErrorMessage());
    }
  }

  @Override
  public AuthToken authenticate(String refreshToken, String email) throws UnauthorisedException {
    Map<String, String> authenticationParameters = new HashMap<>();
    authenticationParameters.put(REFRESH_TOKEN, refreshToken);
    authenticationParameters.put(
        SECRET_HASH, calculateSecretHash(email, userPoolClientId, userPoolClientSecret));
    try {
      return getAuthenticationTokens(REFRESH_TOKEN_AUTH, authenticationParameters);
    } catch (AWSCognitoIdentityProviderException e) {
      log.error(e.getErrorMessage());
      throw new UnauthorisedException(e.getErrorMessage());
    }
  }

  private AuthToken getAuthenticationTokens(
      String authFlowType, Map<String, String> authenticationParameters) {
    InitiateAuthRequest authenticationRequest =
        new InitiateAuthRequest()
            .withAuthFlow(authFlowType)
            .withClientId(userPoolClientId)
            .withAuthParameters(authenticationParameters);
    InitiateAuthResult authenticationResult = cognitoClient.initiateAuth(authenticationRequest);
    return new AuthToken(
        authenticationResult.getAuthenticationResult().getAccessToken(),
        authenticationResult.getAuthenticationResult().getRefreshToken());
  }
}

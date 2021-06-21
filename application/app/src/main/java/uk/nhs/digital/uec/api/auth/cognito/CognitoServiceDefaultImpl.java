package uk.nhs.digital.uec.api.auth.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.auth.exception.InvalidAccessTokenException;
import uk.nhs.digital.uec.api.auth.factory.CognitoClientRequestSecretHashFactory;
import uk.nhs.digital.uec.api.auth.model.AuthTokens;
import uk.nhs.digital.uec.api.auth.model.Credentials;
import uk.nhs.digital.uec.api.auth.util.CheckArgument;
import uk.nhs.digital.uec.api.config.CognitoUserPoolProperties;

/**
 * Implementation of {@link CognitoService} which uses the AWS Cognito identity client {@link
 * AWSCognitoIdentityProvider}. Exceptions are mapped to custom ones.
 */
@Service
@Slf4j
public class CognitoServiceDefaultImpl implements CognitoService {

  private final AWSCognitoIdentityProvider identityClient;

  private final String userPoolClientId;

  private final String userPoolId;

  private final CognitoClientRequestSecretHashFactory secretHashFactory;

  @Autowired
  public CognitoServiceDefaultImpl(
      AWSCognitoIdentityProvider identityClient,
      CognitoUserPoolProperties userPoolProperties,
      CognitoClientRequestSecretHashFactory secretHashFactory) {

    this.identityClient = identityClient;
    this.userPoolId = userPoolProperties.getPoolId();
    this.userPoolClientId = userPoolProperties.getClientId();
    this.secretHashFactory = secretHashFactory;
  }

  /** {@inheritDoc} */
  @Override
  public AuthTokens authenticate(Credentials credentials) throws InvalidAccessTokenException {
    CheckArgument.isNotNull(credentials, "credentials must not be null");
    String emailAddress = credentials.getEmailAddress();
    Map<String, String> authenticationParameters = new HashMap<>();
    authenticationParameters.put("USERNAME", emailAddress);
    authenticationParameters.put("PASSWORD", credentials.getPassword());
    authenticationParameters.put("SECRET_HASH", secretHashFactory.create(emailAddress));
    try {
      return getAuthenticationResult(AuthFlowType.USER_PASSWORD_AUTH, authenticationParameters);
    } catch (AWSCognitoIdentityProviderException e) {
      throw new InvalidAccessTokenException(e.getMessage());
    }
  }

  /** {@inheritDoc} */
  @Override
  public AuthTokens authenticateWithRefreshToken(String refreshToken, String identityProviderId) {
    CheckArgument.hasText(identityProviderId, "identityProviderId must have text");
    CheckArgument.hasText(refreshToken, "refreshToken must have text");
    Map<String, String> authenticationParameters = new HashMap<>();
    authenticationParameters.put("REFRESH_TOKEN", refreshToken);
    authenticationParameters.put("USERNAME", identityProviderId);
    authenticationParameters.put("SECRET_HASH", secretHashFactory.create(identityProviderId));
    try {
      AuthTokens authTokens =
          getAuthenticationResult(AuthFlowType.REFRESH_TOKEN_AUTH, authenticationParameters);
      authTokens.setRefreshToken(refreshToken);
      return authTokens;
    } catch (AWSCognitoIdentityProviderException e) {
      throw new InvalidAccessTokenException(e.getMessage());
    }
  }

  private AuthTokens getAuthenticationResult(
      AuthFlowType authFlowType, Map<String, String> authenticationParameters) {
    InitiateAuthRequest authenticationRequest =
        new InitiateAuthRequest()
            .withAuthFlow(authFlowType)
            .withClientId(userPoolClientId)
            .withAuthParameters(authenticationParameters);
    InitiateAuthResult authenticationResult = identityClient.initiateAuth(authenticationRequest);
    return new AuthTokens(
        authenticationResult.getAuthenticationResult().getAccessToken(),
        authenticationResult.getAuthenticationResult().getRefreshToken());
  }
}

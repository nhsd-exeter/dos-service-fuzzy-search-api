package uk.nhs.digital.uec.api.authentication.cognito;

import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.PASSWORD;
import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.USERNAME;
import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.USER_PASSWORD_AUTH;
import static uk.nhs.digital.uec.api.util.Utils.calculateSecretHash;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import java.util.Arrays;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.authentication.constants.MockAuthenticationConstants;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

@Service
@Slf4j
public class CognitoIdpServiceImpl implements CognitoIdpService {

  @Autowired private AWSCognitoIdentityProvider cognitoClient;
  @Autowired private Environment environment;

  @Value("${cognito.userPool.clientId}")
  private String userPoolClientId;

  @Value(value = "${cognito.userPool.clientSecret}")
  private String userPoolClientSecret;

  @Override
  public AuthToken authenticate(Credential credential) throws UnauthorisedException {
    if (Arrays.asList(environment.getActiveProfiles()).contains(("mock-auth"))) {
      return getMockAuthenticationToken(credential);
    }
    Map<String, String> authenticationParameters =
        Map.of(
            USERNAME,
            credential.getEmailAddress(),
            PASSWORD,
            credential.getPassword(),
            SECRET_HASH,
            calculateSecretHash(
                credential.getEmailAddress(), userPoolClientId, userPoolClientSecret));
    try {
      return getAuthenticationTokens(USER_PASSWORD_AUTH, authenticationParameters);
    } catch (InvalidPasswordException | NotAuthorizedException e) {
      log.error(e.getErrorMessage());
      throw new UnauthorisedException(e.getErrorMessage());
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

  private AuthToken getMockAuthenticationToken(Credential credential) {
    String pass_word = credential.getPassword();
    String userName = credential.getEmailAddress();
    if (userName.equalsIgnoreCase("service-finder-admin@nhs.net")
        && pass_word.equalsIgnoreCase("mock-auth-pass")) {
      log.info("Returning Mock Token");
      return new AuthToken(
          MockAuthenticationConstants.MOCK_ACCESS_TOKEN,
          MockAuthenticationConstants.MOCK_ACCESS_TOKEN);
    } else {
      log.error("Attempted to login using invalid credentials");
      throw new AWSCognitoIdentityProviderException("401 - Unauthorised");
    }
  }
}

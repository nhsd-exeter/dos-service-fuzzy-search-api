package uk.nhs.digital.uec.api.authentication.localstub;

import com.amazonaws.services.cognitoidp.AbstractAWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.authentication.constants.MockAuthenticationConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.PASSWORD;
import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.USERNAME;
import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.COGNITO_GROUP;
import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.ROLE_FUZZY;
import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.ROLE_POSTCODE;

@Slf4j
public class LocalAmazonCognitoIdentityClientStub extends AbstractAWSCognitoIdentityProvider {

  private final Map<String, String> identityProviderIdPasswordMap;

  public LocalAmazonCognitoIdentityClientStub() {
    identityProviderIdPasswordMap = new HashMap<>();
    identityProviderIdPasswordMap.put("admin@nhs.net", "password");
    identityProviderIdPasswordMap.put("service-finder-admin@nhs.net", "mock-auth-pass");
  }

  @Override
  public InitiateAuthResult initiateAuth(InitiateAuthRequest initiateAuthRequest) {
    Map<String, String> authParameters = initiateAuthRequest.getAuthParameters();
    String inputUserName = authParameters.get(USERNAME);
    String inputPassword = authParameters.get(PASSWORD);
    String validPassword = identityProviderIdPasswordMap.get(inputUserName);
    log.info("Login attempted using credentials : " + inputUserName + "/" + inputPassword);

    if (validPassword == null || !validPassword.equals(inputPassword)) {
      log.info("Attempted to login using invalid credentials");
      throw new AWSCognitoIdentityProviderException("401 - Unauthorised");
    } else {
      return initiateAuthRequest(inputUserName);
    }
  }

  private InitiateAuthResult initiateAuthRequest(String userName) {
    AuthenticationResultType authenticationResult = new AuthenticationResultType();
    InitiateAuthResult initiateAuthResult = new InitiateAuthResult();
    if (userName.equalsIgnoreCase("service-finder-admin@nhs.net")) {
      authenticationResult.setAccessToken(MockAuthenticationConstants.MOCK_ACCESS_TOKEN);
      authenticationResult.setRefreshToken(MockAuthenticationConstants.MOCK_ACCESS_TOKEN);
    } else {
      authenticationResult.setAccessToken(generateAuthToken("id", "issuer", userName, 3600000));
      authenticationResult.setRefreshToken(generateAuthToken("rtid", "issuer", userName, 86400000));
    }
    initiateAuthResult.setAuthenticationResult(authenticationResult);
    return initiateAuthResult;
  }

  private String generateAuthToken(String id, String issuer, String userName, long duration) {
    Set<String> cognitoGroupNames =
      new HashSet<>(Arrays.asList(COGNITO_GROUP, ROLE_FUZZY, ROLE_POSTCODE));
    return new LocalJwtFactory().createToken(id, issuer, userName, duration, cognitoGroupNames);
  }
}

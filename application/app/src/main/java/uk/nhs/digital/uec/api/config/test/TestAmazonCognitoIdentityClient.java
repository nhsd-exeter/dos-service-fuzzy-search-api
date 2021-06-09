package uk.nhs.digital.uec.api.config.test;

import com.amazonaws.services.cognitoidp.AbstractAWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.auth.exception.InvalidAuthenticationException;

@Slf4j
public class TestAmazonCognitoIdentityClient extends AbstractAWSCognitoIdentityProvider {

  private static final String AUTH_PARAM_USERNAME = "USERNAME";

  private static final String AUTH_PARAM_PASSWORD = "PASSWORD";

  private static final String ACCEPTED = "Accepted";

  private final Map<String, String> identityProviderIdPasswordMap;

  public TestAmazonCognitoIdentityClient() {
    identityProviderIdPasswordMap = new HashMap<>();
    identityProviderIdPasswordMap.put("admin@nhs.net", "password");
  }

  @Override
  public InitiateAuthResult initiateAuth(InitiateAuthRequest initiateAuthRequest) {
    Map<String, String> authParameters = initiateAuthRequest.getAuthParameters();
    String username = authParameters.get(AUTH_PARAM_USERNAME);
    String password = authParameters.get(AUTH_PARAM_PASSWORD);
    log.info("Login attempt from : " + username + "/" + password);

    if (!identityProviderIdPasswordMap.get(username).equals(password)) {
      throw new InvalidAuthenticationException("Invalid Authentication");
    }

    log.info(ACCEPTED);
    // create set and add value
    Set<String> groupNames = new HashSet<>(Arrays.asList("API_USER"));

    AuthenticationResultType authenticationResult = new AuthenticationResultType();
    TestJwtFactory testJwtFactory = new TestJwtFactory();
    String accessToken = testJwtFactory.create("id", "issuer", username, 3600000, groupNames);
    authenticationResult.setAccessToken(accessToken);
    String refreshToken =
        testJwtFactory.create("rtid", "issuer", username, 86400000, new HashSet<>());
    authenticationResult.setRefreshToken(refreshToken);
    InitiateAuthResult initiateAuthResult = new InitiateAuthResult();
    initiateAuthResult.setAuthenticationResult(authenticationResult);
    return initiateAuthResult;
  }
}

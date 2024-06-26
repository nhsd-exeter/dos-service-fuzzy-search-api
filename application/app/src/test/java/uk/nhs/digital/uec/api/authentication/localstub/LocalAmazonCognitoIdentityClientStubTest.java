package uk.nhs.digital.uec.api.authentication.localstub;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.PASSWORD;
import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.USERNAME;
import static uk.nhs.digital.uec.api.util.Utils.calculateSecretHash;

import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocalAmazonCognitoIdentityClientStubTest {

  private LocalAmazonCognitoIdentityClientStub amazonCognitoIdentityClientStub;

  @BeforeEach
  public void setup() {
    amazonCognitoIdentityClientStub = new LocalAmazonCognitoIdentityClientStub();
  }

  @Test
  void initiateAuthRequestTest() {
    // ...

    Map<String, String> authenticationParameters = new HashMap<>();
    authenticationParameters.put(USERNAME, "admin@nhs.net");
    authenticationParameters.put(PASSWORD, "password");
    authenticationParameters.put(
        "SECRET_HASH",
        calculateSecretHash("admin@nhs.net", "testUserPoolClientId", "testUserPoolClientSecret"));

    InitiateAuthRequest authenticationRequest =
        new InitiateAuthRequest()
            .withAuthFlow("authFlowType")
            .withClientId("testUserPoolClientId")
            .withAuthParameters(authenticationParameters);

    InitiateAuthResult initiateAuth =
        amazonCognitoIdentityClientStub.initiateAuth(authenticationRequest);
    assertNotNull(initiateAuth.getAuthenticationResult().getAccessToken());
    assertNotNull(initiateAuth.getAuthenticationResult().getRefreshToken());
  }

  @Test
  public void initiateMockAuthRequestTest() {
    Map<String, String> authenticationParameters = new HashMap<>();
    authenticationParameters.put(USERNAME, "service-finder-admin@nhs.net");
    authenticationParameters.put(PASSWORD, "mock-auth-pass");
    authenticationParameters.put(
        "SECRET_HASH",
        calculateSecretHash(
            "service-finder-admin@nhs.net", "testUserPoolClientId", "testUserPoolClientSecret"));

    InitiateAuthRequest authenticationRequest =
        new InitiateAuthRequest()
            .withAuthFlow("authFlowType")
            .withClientId("testUserPoolClientId")
            .withAuthParameters(authenticationParameters);

    InitiateAuthResult initiateAuth =
        amazonCognitoIdentityClientStub.initiateAuth(authenticationRequest);
    assertNotNull(initiateAuth.getAuthenticationResult().getAccessToken());
    assertNotNull(initiateAuth.getAuthenticationResult().getRefreshToken());
  }
}

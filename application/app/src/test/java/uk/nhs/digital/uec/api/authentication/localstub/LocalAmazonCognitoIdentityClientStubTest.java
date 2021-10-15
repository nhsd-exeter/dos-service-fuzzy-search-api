package uk.nhs.digital.uec.api.authentication.localstub;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocalAmazonCognitoIdentityClientStubTest {

  LocalAmazonCognitoIdentityClientStub localAmazonCognitoIdentityClientStub;

  private InitiateAuthRequest initiateAuthRequest;

  @BeforeEach
  private void initialize() {
    localAmazonCognitoIdentityClientStub = new LocalAmazonCognitoIdentityClientStub();
    initiateAuthRequest = new InitiateAuthRequest();
  }

  @Test
  public void initiateAuthTest() {
    initiateAuthRequest.addAuthParametersEntry("USERNAME", "admin@nhs.net");
    InitiateAuthResult initiateAuth =
        localAmazonCognitoIdentityClientStub.initiateAuth(initiateAuthRequest);
    assertNotNull(initiateAuth.getAuthenticationResult().getAccessToken());
    assertNotNull(initiateAuth.getAuthenticationResult().getRefreshToken());
  }

  @Test
  public void initiateAuthInvalidCredentialsTest() {
    initiateAuthRequest.addAuthParametersEntry("USERNAME-INVALID", "admin@nhs.net");
    assertThrows(
        AWSCognitoIdentityProviderException.class,
        () -> localAmazonCognitoIdentityClientStub.initiateAuth(initiateAuthRequest));
  }
}

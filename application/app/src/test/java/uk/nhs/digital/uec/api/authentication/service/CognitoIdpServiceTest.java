package uk.nhs.digital.uec.api.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.digital.uec.api.authentication.cognito.CognitoIdpServiceImpl;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

@ExtendWith(SpringExtension.class)
public class CognitoIdpServiceTest {

  @InjectMocks private CognitoIdpServiceImpl cognitoIdpService;

  @Mock private AWSCognitoIdentityProvider cognitoClient;

  private AuthToken authToken;
  private Credential credential;
  private String accessToken;
  private String refreshToken;
  @Mock private AuthenticationResultType authenticationResult;
  private InitiateAuthResult initiateAuthResult;

  @BeforeEach
  private void initialize() {
    authToken = new AuthToken();
    accessToken = "123452AcEss-ToKen-Sample";
    authToken.setAccessToken(accessToken);
    refreshToken = "123452AcEss-Refresh-ToKen-Sample";
    authToken.setRefreshToken(refreshToken);
    credential = new Credential();
    credential.setEmailAddress("someUser@.xyz.net");
    credential.setPassword("somePassword");

    authenticationResult = new AuthenticationResultType();
    authenticationResult.setAccessToken(accessToken);
    authenticationResult.setRefreshToken(refreshToken);
    initiateAuthResult = new InitiateAuthResult();
    initiateAuthResult.setAuthenticationResult(authenticationResult);
    ReflectionTestUtils.setField(cognitoIdpService, "userPoolClientId", "testUserPoolClientId");
    ReflectionTestUtils.setField(
        cognitoIdpService, "userPoolClientSecret", "testUserPoolClientSecret");
  }

  @Test
  public void testCognitoAuthentication() throws UnauthorisedException {
    doReturn(initiateAuthResult).when(cognitoClient).initiateAuth(any());
    AuthToken returnedAuthToken = cognitoIdpService.authenticate(credential);
    assertEquals(accessToken, returnedAuthToken.getAccessToken());
  }

  @Test
  public void testAuthenticationServiceForRefreshToken() throws UnauthorisedException {
    doReturn(initiateAuthResult).when(cognitoClient).initiateAuth(any());
    AuthToken returnedAuthToken =
        cognitoIdpService.authenticateWithRefreshToken(refreshToken, credential.getEmailAddress());
    assertEquals(accessToken, returnedAuthToken.getAccessToken());
  }
}

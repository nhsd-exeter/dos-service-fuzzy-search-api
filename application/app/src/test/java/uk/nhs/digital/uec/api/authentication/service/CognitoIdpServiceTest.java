package uk.nhs.digital.uec.api.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
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
import uk.nhs.digital.uec.api.authentication.exception.InvalidAccessTokenException;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

@ExtendWith(SpringExtension.class)
public class CognitoIdpServiceTest {

  @InjectMocks private CognitoIdpServiceImpl cognitoIdpService;

  @Mock private AWSCognitoIdentityProvider cognitoClient;

  private Credential credential;
  private String accessToken;
  private String refreshToken;
  @Mock private AuthenticationResultType authenticationResult;
  private InitiateAuthResult initiateAuthResult;

  @BeforeEach
  private void initialize() {
    accessToken =
        "eyJqdGkiOiJpZCIsImlhdCI6MTYyNjc3NTgyMywic3ViIjoiYWRtaW5AbmhzLm5ldCIsImlzcyI-Access-ToKen";
    refreshToken = "mlhdCI6MTYyNjc3NTgyMywic3ViIjoiY-RtaW5AbmhzLm5ldCIsImlzcy-Refresh-ToKen";
    credential = new Credential();
    credential.setEmailAddress("xyzUser@.xyz.net");
    credential.setPassword("xyzPassword");

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

  @Test
  public void testGetAuthentiocationTokenTest() throws UnauthorisedException {
    doThrow(new AWSCognitoIdentityProviderException("Cognito error"))
        .when(cognitoClient)
        .initiateAuth(any());
    assertThrows(
        InvalidAccessTokenException.class,
        () ->
            cognitoIdpService.authenticateWithRefreshToken(
                refreshToken, credential.getEmailAddress()));
  }
}

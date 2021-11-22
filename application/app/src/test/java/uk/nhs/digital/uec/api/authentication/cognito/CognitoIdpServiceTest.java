package uk.nhs.digital.uec.api.authentication.cognito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
public class CognitoIdpServiceTest {

  @InjectMocks CognitoIdpServiceImpl cognitoService;
  @Mock AWSCognitoIdentityProvider cognitoClient;
  private String userPoolClientId = "testUserPoolClientId";
  private String userPoolClientSecret = "testUserPoolClientSecret";
  private String user;
  private String userPass;
  private String accessToken;
  private String refreshToken;

  @BeforeEach
  public void setup() {
    ReflectionTestUtils.setField(cognitoService, "userPoolClientId", userPoolClientId);
    ReflectionTestUtils.setField(cognitoService, "userPoolClientSecret", userPoolClientSecret);
    user = "admin@nhs.net";
    userPass = "password";
    accessToken = "ACCESS_TOKEN_123";
    refreshToken = "REFRESH_TOKEN_123";
  }

  @Test
  public void authenticationPositiveTest() throws UnauthorisedException {
    Credential cred = new Credential(user, userPass);
    InitiateAuthResult authResult = new InitiateAuthResult();
    AuthenticationResultType authenticationResult = new AuthenticationResultType();
    authenticationResult.setAccessToken(accessToken);
    authenticationResult.setRefreshToken(refreshToken);
    authResult.setAuthenticationResult(authenticationResult);

    when(cognitoClient.initiateAuth(any())).thenReturn(authResult);
    AuthToken accessTokenResponse = cognitoService.authenticate(cred);
    assertNotNull(accessTokenResponse.getAccessToken());
  }

  @Test
  public void authenticationInvalidPasswordExceptionTest() throws UnauthorisedException {
    Credential cred = new Credential(user, userPass);
    when(cognitoClient.initiateAuth(any())).thenThrow(InvalidPasswordException.class);
    assertThrows(UnauthorisedException.class, () -> cognitoService.authenticate(cred));
  }

  @Test
  public void authenticationNotAuthorizedExceptionTest() throws UnauthorisedException {
    Credential cred = new Credential(user, userPass);
    when(cognitoClient.initiateAuth(any())).thenThrow(NotAuthorizedException.class);
    assertThrows(UnauthorisedException.class, () -> cognitoService.authenticate(cred));
  }

  @Test
  public void authenticationAWSCognitoIdentityProviderExceptionTest() throws UnauthorisedException {
    Credential cred = new Credential(user, userPass);
    when(cognitoClient.initiateAuth(any())).thenThrow(AWSCognitoIdentityProviderException.class);
    assertThrows(UnauthorisedException.class, () -> cognitoService.authenticate(cred));
  }
}

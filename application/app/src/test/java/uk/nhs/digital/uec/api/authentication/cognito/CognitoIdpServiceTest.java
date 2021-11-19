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

  @BeforeEach
  public void setup() {
    ReflectionTestUtils.setField(cognitoService, "userPoolClientId", userPoolClientId);
    ReflectionTestUtils.setField(cognitoService, "userPoolClientSecret", userPoolClientSecret);
  }

  @Test
  public void authenticationPositiveTest() throws UnauthorisedException {
    AuthToken authToken = new AuthToken();
    Credential cred = new Credential("admin@nhs.net", "password");
    authToken.setAccessToken("ACCESS_TOKEN_123");
    authToken.setRefreshToken("REFRESH_TOKEN_123");

    InitiateAuthResult authResult = new InitiateAuthResult();
    AuthenticationResultType authenticationResult = new AuthenticationResultType();
    authenticationResult.setAccessToken("access-from-cognito");
    authenticationResult.setRefreshToken("refresh-from-cognito");
    authResult.setAuthenticationResult(authenticationResult);

    when(cognitoClient.initiateAuth(any())).thenReturn(authResult);
    AuthToken accessToken = cognitoService.authenticate(cred);

    assertNotNull(accessToken.getAccessToken());
  }

  @Test
  public void authenticationInvalidPasswordExceptionTest() throws UnauthorisedException {
    AuthToken authToken = new AuthToken();
    Credential cred = new Credential("admin@nhs.net", "password");
    authToken.setAccessToken("ACCESS_TOKEN_123");
    authToken.setRefreshToken("REFRESH_TOKEN_123");

    InitiateAuthResult authResult = new InitiateAuthResult();
    AuthenticationResultType authenticationResult = new AuthenticationResultType();
    authenticationResult.setAccessToken("access-from-cognito");
    authenticationResult.setRefreshToken("refresh-from-cognito");
    authResult.setAuthenticationResult(authenticationResult);

    when(cognitoClient.initiateAuth(any())).thenThrow(InvalidPasswordException.class);
    assertThrows(
        UnauthorisedException.class,
        () -> {
          cognitoService.authenticate(cred);
        });
  }

  @Test
  public void authenticationNotAuthorizedExceptionTest() throws UnauthorisedException {
    AuthToken authToken = new AuthToken();
    Credential cred = new Credential("admin@nhs.net", "password");
    authToken.setAccessToken("ACCESS_TOKEN_123");
    authToken.setRefreshToken("REFRESH_TOKEN_123");

    InitiateAuthResult authResult = new InitiateAuthResult();
    AuthenticationResultType authenticationResult = new AuthenticationResultType();
    authenticationResult.setAccessToken("access-from-cognito");
    authenticationResult.setRefreshToken("refresh-from-cognito");
    authResult.setAuthenticationResult(authenticationResult);

    when(cognitoClient.initiateAuth(any())).thenThrow(NotAuthorizedException.class);
    assertThrows(
        UnauthorisedException.class,
        () -> {
          cognitoService.authenticate(cred);
        });
  }

  @Test
  public void authenticationAWSCognitoIdentityProviderExceptionTest() throws UnauthorisedException {
    AuthToken authToken = new AuthToken();
    Credential cred = new Credential("admin@nhs.net", "password");
    authToken.setAccessToken("ACCESS_TOKEN_123");
    authToken.setRefreshToken("REFRESH_TOKEN_123");

    InitiateAuthResult authResult = new InitiateAuthResult();
    AuthenticationResultType authenticationResult = new AuthenticationResultType();
    authenticationResult.setAccessToken("access-from-cognito");
    authenticationResult.setRefreshToken("refresh-from-cognito");
    authResult.setAuthenticationResult(authenticationResult);

    when(cognitoClient.initiateAuth(any())).thenThrow(AWSCognitoIdentityProviderException.class);
    assertThrows(
        UnauthorisedException.class,
        () -> {
          cognitoService.authenticate(cred);
        });
  }
}

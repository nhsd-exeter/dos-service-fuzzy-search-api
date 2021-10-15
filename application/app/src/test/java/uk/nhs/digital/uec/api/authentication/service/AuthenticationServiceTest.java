package uk.nhs.digital.uec.api.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.authentication.cognito.CognitoIdpServiceImpl;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

@ExtendWith(SpringExtension.class)
public class AuthenticationServiceTest {

  @InjectMocks private AuthenticationServiceImpl authenticationService;
  @Mock private CognitoIdpServiceImpl cognitoIdpService;

  private AuthToken authToken;
  private Credential credential;
  private String accessToken;
  private String refreshToken;

  @BeforeEach
  private void initialize() {
    authToken = new AuthToken();
    accessToken = "123452AcEss-ToKen-Sample";
    authToken.setAccessToken(accessToken);
    refreshToken = "123452AcEss-Refresh-ToKen-Sample";
    authToken.setRefreshToken(refreshToken);
    credential = new Credential();
    credential.setEmailAddress("mock-user@xyz.com");
    credential.setPassword("pAssWord");
  }

  @Test
  public void testAuthenticationService() throws UnauthorisedException {
    doReturn(authToken).when(cognitoIdpService).authenticate(credential);
    AuthToken returnedAuthToken = authenticationService.getAccessToken(credential);
    assertEquals(accessToken, returnedAuthToken.getAccessToken());
  }

  @Test
  public void testAuthenticationServiceForRefreshToken() throws UnauthorisedException {
    doReturn(authToken)
        .when(cognitoIdpService)
        .authenticateWithRefreshToken(anyString(), anyString());
    AuthToken returnedAuthToken = authenticationService.refreshToken(anyString(), anyString());
    assertEquals(accessToken, returnedAuthToken.getAccessToken());
  }
}

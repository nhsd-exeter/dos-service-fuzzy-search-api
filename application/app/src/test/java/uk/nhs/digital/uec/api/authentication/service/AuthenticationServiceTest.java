package uk.nhs.digital.uec.api.authentication.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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

  @InjectMocks AuthenticationService authService;
  @Mock CognitoIdpServiceImpl cognitoIdpService;

  @Test
  public void getAccessTokenTest() throws UnauthorisedException {
    AuthToken authToken = new AuthToken();
    Credential cred = new Credential("admin@nhs.net", "password");
    authToken.setAccessToken("ACCESS_TOKEN_123");
    authToken.setRefreshToken("REFRESH_TOKEN_123");
    when(cognitoIdpService.authenticate(cred)).thenReturn(authToken);
    AuthToken accessToken = authService.getAccessToken(cred);

    assertNotNull(accessToken.getAccessToken());
  }
}

package uk.nhs.digital.uec.api.authentication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.authentication.service.AuthenticationService;
import uk.nhs.digital.uec.api.exception.NotFoundException;

@ExtendWith(SpringExtension.class)
public class LoginControllerTest {

  @InjectMocks LoginController loginController;
  @Mock AuthenticationService authenticationService;
  String refreshToken = "REFRESH_TOKEN_123";
  AuthToken authToken;
  String email;
  Credential cred;

  @BeforeEach
  public void setup() {
    authToken = new AuthToken();
    authToken.setAccessToken("ACCESS_TOKEN_123");
    authToken.setRefreshToken("REFRESH_TOKEN_123");
    email = "admin@nhs.net";
    cred = new Credential("admin1@nhs.net", "password1");
  }

  @Test
  public void loginTest() throws NotFoundException, UnauthorisedException {
    when(authenticationService.getAccessToken(cred)).thenReturn(authToken);
    ResponseEntity response = loginController.getAccessToken(cred);
    AuthToken authTokenResponse = (AuthToken) response.getBody();
    assertNotNull(authTokenResponse.getAccessToken());
  }

  @Test
  public void loginRefreshTest() throws NotFoundException, UnauthorisedException {
    when(authenticationService.getAccessToken(refreshToken, cred.getEmailAddress()))
        .thenReturn(authToken);
    ResponseEntity response = loginController.getAccessToken(refreshToken, cred);
    AuthToken authTokenResponse = (AuthToken) response.getBody();
    assertNotNull(authTokenResponse.getAccessToken());
  }

  @Test
  public void loginExceptionTest() throws NotFoundException, UnauthorisedException {
    Credential cred = new Credential("admin2@nhs.net", "password2");
    when(authenticationService.getAccessToken(cred)).thenThrow(UnauthorisedException.class);
    ResponseEntity response = loginController.getAccessToken(cred);
    assertEquals(response.getStatusCode().value(), HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  public void loginRefreshExceptionTest() throws NotFoundException, UnauthorisedException {
    when(authenticationService.getAccessToken(refreshToken, cred.getEmailAddress()))
        .thenThrow(UnauthorisedException.class);
    ResponseEntity response = loginController.getAccessToken(refreshToken, cred);
    assertEquals(response.getStatusCode().value(), HttpStatus.UNAUTHORIZED.value());
  }
}

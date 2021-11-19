package uk.nhs.digital.uec.api.authentication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.nhs.digital.uec.api.exception.ValidationException;

@ExtendWith(SpringExtension.class)
public class LoginControllerTest {

  @InjectMocks LoginController loginController;
  @Mock AuthenticationService authenticationService;

  @Test
  public void loginTest() throws ValidationException, UnauthorisedException {
    AuthToken authToken = new AuthToken();
    Credential cred = new Credential("admin@nhs.net", "password");
    authToken.setAccessToken("ACCESS_TOKEN_123");
    authToken.setRefreshToken("REFRESH_TOKEN_123");
    ObjectMapper mapper = new ObjectMapper();
    when(authenticationService.getAccessToken(cred)).thenReturn(authToken);

    ResponseEntity response = loginController.getAccessToken(cred);
    AuthToken authTokenResponse = (AuthToken) response.getBody();
    assertNotNull(authTokenResponse.getAccessToken());
  }

  @Test
  public void loginExceptionTest() throws ValidationException, UnauthorisedException {
    AuthToken authToken = new AuthToken();
    Credential cred = new Credential("admin@nhs.net", "password");
    authToken.setAccessToken("ACCESS_TOKEN_123");
    authToken.setRefreshToken("REFRESH_TOKEN_123");
    ObjectMapper mapper = new ObjectMapper();
    when(authenticationService.getAccessToken(cred)).thenThrow(UnauthorisedException.class);
    ResponseEntity response = loginController.getAccessToken(cred);

    assertEquals(response.getStatusCode().value(), HttpStatus.UNAUTHORIZED.value());
  }
}

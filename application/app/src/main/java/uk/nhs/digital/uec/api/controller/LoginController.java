package uk.nhs.digital.uec.api.controller;

import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.authentication.exception.InvalidCredentialsException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.service.AuthenticationServiceInterface;

@RestController
@AllArgsConstructor
public class LoginController {

  private final AuthenticationServiceInterface authenticationService;

  @PostMapping("/authentication/login")
  public ResponseEntity<AuthToken> getAccessToken(
      @RequestBody Credential credentials, HttpServletResponse response) {
    AuthToken resultPayload = null;
    try {
      resultPayload = authenticationService.getAccessToken(credentials);
    } catch (InvalidCredentialsException ex) {
      resultPayload = new AuthToken();
      resultPayload.setMessage(ex.getMessage());
    }
    return ResponseEntity.ok(resultPayload);
  }
}

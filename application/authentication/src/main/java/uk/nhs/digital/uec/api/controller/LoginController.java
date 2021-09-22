package uk.nhs.digital.uec.api.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;
import uk.nhs.digital.uec.api.service.AuthenticationService;

@RestController
public class LoginController {
  @Autowired private AuthenticationService authenticationService;

  @PostMapping("/authentication/login")
  public ResponseEntity getAccessToken(
      @RequestBody Credential credentials, HttpServletResponse response) {
    AuthToken resultPayload = null;
    try {
      resultPayload = authenticationService.getAccessToken(credentials);
    } catch (UnauthorisedException ex) {
      return ResponseEntity.status(UNAUTHORIZED.value()).body(ex.getMessage());
    }
    return ResponseEntity.ok(resultPayload);
  }
}

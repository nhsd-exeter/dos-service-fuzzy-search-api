package uk.nhs.digital.uec.api.authentication.controller;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.authentication.service.AuthenticationService;

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
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(ex.getMessage());
    }
    return ResponseEntity.ok(resultPayload);
  }
}

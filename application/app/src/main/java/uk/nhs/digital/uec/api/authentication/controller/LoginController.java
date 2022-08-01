package uk.nhs.digital.uec.api.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.authentication.service.AuthenticationServiceInterface;

@RestController
public class LoginController {

  @Autowired private AuthenticationServiceInterface authenticationService;

  @PostMapping("/authentication/login")
  public ResponseEntity getAccessToken(@RequestBody Credential credentials) {
    AuthToken resultPayload = null;
    try {
      resultPayload = authenticationService.getAccessToken(credentials);
    } catch (UnauthorisedException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(ex.getMessage());
    }
    return ResponseEntity.ok(resultPayload);
  }

  @PostMapping("/authentication/refresh")
  public ResponseEntity getAccessToken(@RequestHeader("REFRESH-TOKEN") String refreshToken, @RequestBody Credential credential){
    AuthToken resultPayload = null;
    try{
      resultPayload = authenticationService.getAccessToken(refreshToken, credential);
    }catch (UnauthorisedException ex){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
    return ResponseEntity.ok(resultPayload);
  }
}

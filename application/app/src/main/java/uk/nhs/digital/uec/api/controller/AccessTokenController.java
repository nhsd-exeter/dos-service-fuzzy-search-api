package uk.nhs.digital.uec.api.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.auth.exception.NoRolesException;
import uk.nhs.digital.uec.api.auth.factory.CookieFactory;
import uk.nhs.digital.uec.api.auth.model.AuthTokens;
import uk.nhs.digital.uec.api.auth.model.Credentials;
import uk.nhs.digital.uec.api.service.AccessTokenServiceInterface;

/** Simple welcome/home page controller for the DoS Service Fuzzy Search API. */
@RestController
@AllArgsConstructor
@Slf4j
public class AccessTokenController {

  private final AccessTokenServiceInterface authTokenService;

  private final CookieFactory cookieFactory;

  @PostMapping("/dosapi/accesstoken")
  public ResponseEntity<String> getAccessToken(
      @RequestBody Credentials credentials, HttpServletResponse response) throws NoRolesException {
    AuthTokens resultPayload = authTokenService.accessToken(credentials);
    Cookie accessTokenCookie = cookieFactory.createAccessToken(resultPayload.getAccessToken());
    response.addCookie(accessTokenCookie);
    Cookie refreshTokenCookie = cookieFactory.createRefreshToken(resultPayload.getRefreshToken());
    response.addCookie(refreshTokenCookie);
    return ResponseEntity.ok(resultPayload.getAccessToken());
  }
}

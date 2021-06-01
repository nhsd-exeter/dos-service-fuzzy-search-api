package uk.nhs.digital.uec.api.auth.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.auth.exception.AccessTokenExpiredException;
import uk.nhs.digital.uec.api.auth.util.CheckArgument;

/** Service that checks whether an access token has expired */
@Slf4j
public class AccessTokenChecker {

  private JwtDecoder decoder;

  public AccessTokenChecker(JwtDecoder decoder) {
    this.decoder = decoder;
  }

  /**
   * Check whether an access token has expired.
   *
   * @param accessToken the access token to check
   * @throws AccessTokenExpiredException if the access token is expired.
   * @throws IllegalStateException if the access token cannot be decoded.
   */
  public void isValid(String accessToken) throws AccessTokenExpiredException {
    CheckArgument.hasText(accessToken, "accessToken must have text");
    DecodedJWT jwt;
    try {
      jwt = decoder.decode(accessToken);
    } catch (JWTDecodeException e) {
      log.info("Failed to decode access tokens", e);
      throw new IllegalStateException(e);
    }
    if (jwt.getExpiresAt().before(new Date())) {
      throw new AccessTokenExpiredException();
    }
  }
}

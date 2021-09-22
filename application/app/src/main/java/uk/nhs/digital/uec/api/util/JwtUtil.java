package uk.nhs.digital.uec.api.util;

import static uk.nhs.digital.uec.api.util.Constants.CLAIM_NAME;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import uk.nhs.digital.uec.api.exception.AccessTokenExpiredException;

@Component
@Slf4j
public class JwtUtil {

  /**
   * Check whether an access token has expired.
   *
   * @param accessToken the access token to check
   * @throws AccessTokenExpiredException if the access token is expired.
   * @throws IllegalStateException if the access token cannot be decoded.
   */
  public void isTokenValid(String accessToken) throws AccessTokenExpiredException {
    if (accessToken == null) return;
    DecodedJWT jwt;
    try {
      jwt = JWT.decode(accessToken);
    } catch (JWTDecodeException e) {
      log.error("Failed to decode access token", e);
      throw new IllegalStateException();
    }
    if (jwt.getExpiresAt().before(new Date())) {
      log.error("Access Token has expired");
      throw new AccessTokenExpiredException("Access Token has expired");
    }
  }

  public String getUserNameFromToken(String accessToken) {
    return JWT.decode(accessToken).getClaim(CLAIM_NAME).asString();
  }

  public String getTokenFromHeader(HttpServletRequest request) {
    String token = null;
    final String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);
    }
    return token;
  }

  public String getIdentityProviderIdDigest(String identityProviderId) {
    return DigestUtils.sha1Hex(identityProviderId);
  }

  public String convertObjectToJson(Object object) throws JsonProcessingException {
    return object == null ? null : new ObjectMapper().writeValueAsString(object);
  }
}

package uk.nhs.digital.uec.api.auth.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

/** Class to wrap the static JWT utility method in (mockable) object */
public class JwtDecoder {

  public DecodedJWT decode(String jwt) {
    return JWT.decode(jwt);
  }
}

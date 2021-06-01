package uk.nhs.digital.uec.api.auth.factory;

import static uk.nhs.digital.uec.api.auth.AuthConstants.ACCESS_TOKEN;
import static uk.nhs.digital.uec.api.auth.AuthConstants.REFRESH_TOKEN;

import javax.servlet.http.Cookie;
import org.apache.commons.lang3.StringUtils;

/** Factory capable of creating {@link Cookie}s */
public class CookieFactory {

  private static final int COOKIE_MAX_AGE = 86400;

  private static final int COOKIE_EXPIRED_AGE = 0;

  private final String cookieDomain;

  /**
   * Constructs an instance with the required properties
   *
   * @param cookieDomain Domain for the cookie
   */
  public CookieFactory(String cookieDomain) {
    this.cookieDomain = cookieDomain;
  }

  /**
   * Returns an access token cookie, can be used for both setting or clearing an access token cookie
   *
   * @param value the value of the cookie, can be null
   * @return an access token cookie
   */
  public Cookie createAccessToken(String value) {
    return create(ACCESS_TOKEN, value);
  }

  /**
   * Returns a refresh token cookie, can be used for both setting or clearing a refresh token cookie
   *
   * @param value the value of the cookie, can be null
   * @return a refresh token cookie
   */
  public Cookie createRefreshToken(String value) {
    return create(REFRESH_TOKEN, value);
  }

  private Cookie create(String cookieName, String value) {
    Cookie cookie = new Cookie(cookieName, value);
    if (StringUtils.isBlank(value)) {
      cookie.setMaxAge(COOKIE_EXPIRED_AGE);
    } else {
      cookie.setMaxAge(COOKIE_MAX_AGE);
    }
    cookie.setPath("/");
    cookie.setSecure(true);
    cookie.setHttpOnly(true);
    cookie.setDomain(cookieDomain);
    return cookie;
  }
}

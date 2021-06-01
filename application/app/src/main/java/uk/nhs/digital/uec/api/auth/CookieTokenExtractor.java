package uk.nhs.digital.uec.api.auth;

import static uk.nhs.digital.uec.api.auth.AuthConstants.ACCESS_TOKEN;
import static uk.nhs.digital.uec.api.auth.AuthConstants.REFRESH_TOKEN;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import uk.nhs.digital.uec.api.auth.model.AuthenticationToken;

public class CookieTokenExtractor implements TokenExtractor {

  @Override
  public Authentication extract(HttpServletRequest request) {
    String tokenValue = extractToken(request, ACCESS_TOKEN);
    if (tokenValue != null) {
      PreAuthenticatedAuthenticationToken authentication =
          new PreAuthenticatedAuthenticationToken(tokenValue, "");
      return authentication;
    }
    return null;
  }

  /**
   * Extracts the ACCESS and REFRESH tokens from the request and passes them back out as a {@link
   * AuthenticationToken}.
   *
   * @param request the {@link HttpServletRequest} to extract the tokens from
   * @return {@link AuthenticationToken}
   */
  public static AuthenticationToken extractAuthenticationToken(final HttpServletRequest request) {
    return new AuthenticationToken(
        extractToken(request, ACCESS_TOKEN), extractToken(request, REFRESH_TOKEN));
  }

  private static String extractToken(HttpServletRequest request, String tokenName) {
    String token = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (tokenName.equals(cookie.getName())) {
          token = cookie.getValue();
        }
      }
    }
    return token;
  }
}

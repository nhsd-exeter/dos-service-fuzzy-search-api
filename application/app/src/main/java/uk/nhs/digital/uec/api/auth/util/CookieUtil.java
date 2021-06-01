package uk.nhs.digital.uec.api.auth.util;

import java.util.Arrays;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import uk.nhs.digital.uec.api.auth.model.AuthenticationToken;

public class CookieUtil {

  public static String getCookieValue(HttpServletRequest req, String cookieName) {
    if (req.getCookies() == null) {
      return null;
    }
    return Arrays.stream(req.getCookies())
        .filter(c -> c.getName().equals(cookieName))
        .findFirst()
        .map(Cookie::getValue)
        .orElse(null);
  }

  public static HttpHeaders addAuthTokensToCookieHeader(
      final AuthenticationToken authenticationToken) {

    final HttpHeaders headers = new HttpHeaders();
    if (authenticationToken != null) {
      headers.add("Cookie", "ACCESS_TOKEN=" + authenticationToken.getAccessToken());
      headers.add("Cookie", "REFRESH_TOKEN=" + authenticationToken.getRefreshToken());
    }
    return headers;
  }
}

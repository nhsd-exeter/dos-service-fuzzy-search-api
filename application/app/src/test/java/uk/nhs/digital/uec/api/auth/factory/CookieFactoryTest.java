package uk.nhs.digital.uec.api.auth.factory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.nhs.digital.uec.api.auth.AuthConstants.ACCESS_TOKEN;
import static uk.nhs.digital.uec.api.auth.AuthConstants.REFRESH_TOKEN;
import static uk.nhs.digital.uec.api.auth.testsupport.CookieMatcher.cookieMatching;

import javax.servlet.http.Cookie;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Test for {@link CookieFactory} */
@RunWith(JUnitParamsRunner.class)
public class CookieFactoryTest {

  @Test
  @Parameters({"TestAccessTokenValue, 86400", " , 0"})
  public void shouldCreateAccessToken(String cookieValue, int cookieMaxAge) {
    // Given
    String cookieDomain = "example.org";
    CookieFactory factory = new CookieFactory(cookieDomain);

    // When
    final Cookie cookie = factory.createAccessToken(cookieValue);

    // Then
    Cookie expectedCookie =
        getExpectedCookie(cookieMaxAge, cookieDomain, cookieValue, ACCESS_TOKEN);
    assertThat(cookie, is(cookieMatching(expectedCookie)));
  }

  @Test
  @Parameters({"TestRefreshTokenValue, 86400", " , 0"})
  public void shouldCreateRefreshToken(String cookieValue, int cookieMaxAge) {
    // Given
    String cookieDomain = "example.org";
    CookieFactory factory = new CookieFactory(cookieDomain);

    // When
    final Cookie cookie = factory.createRefreshToken(cookieValue);

    // Then
    Cookie expectedCookie =
        getExpectedCookie(cookieMaxAge, cookieDomain, cookieValue, REFRESH_TOKEN);
    assertThat(cookie, is(cookieMatching(expectedCookie)));
  }

  private Cookie getExpectedCookie(
      int cookieMaxAge, String cookieDomain, String cookieValue, String refreshToken) {
    Cookie expectedCookie = new Cookie(refreshToken, cookieValue);
    expectedCookie.setPath("/");
    expectedCookie.setSecure(true);
    expectedCookie.setHttpOnly(true);
    expectedCookie.setMaxAge(cookieMaxAge);
    expectedCookie.setDomain(cookieDomain);
    return expectedCookie;
  }
}

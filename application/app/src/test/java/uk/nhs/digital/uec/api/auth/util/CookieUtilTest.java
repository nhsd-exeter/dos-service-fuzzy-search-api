package uk.nhs.digital.uec.api.auth.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import uk.nhs.digital.uec.api.auth.model.AuthenticationToken;

@RunWith(MockitoJUnitRunner.class)
public class CookieUtilTest {

  private static final String ACCESS_TOKEN = "TEST_ACCESS_TOKEN";

  private static final String REFRESH_TOKEN = "TEST_REFRESH_TOKEN";

  private static final String ACCESS_COOKIE_TOKEN = "ACCESS_TOKEN=" + ACCESS_TOKEN;

  private static final String REFRESH_COOKIE_TOKEN = "REFRESH_TOKEN=" + REFRESH_TOKEN;

  private AuthenticationToken authenticationToken =
      new AuthenticationToken(ACCESS_TOKEN, REFRESH_TOKEN);

  @Test
  public void addAuthTokensToCookieHeaderForPopulatedAuthenticationToken() {
    HttpHeaders headers = CookieUtil.addAuthTokensToCookieHeader(authenticationToken);

    List<String> cookies = headers.get("Cookie");
    assertTrue(cookies.contains(ACCESS_COOKIE_TOKEN));
    assertTrue(cookies.contains(REFRESH_COOKIE_TOKEN));
  }

  @Test
  public void addAuthTokensToCookieHeaderForNullAuthenticationToken() {
    HttpHeaders headers = CookieUtil.addAuthTokensToCookieHeader(null);

    assertFalse(headers.containsKey("Cookie"));
  }
}

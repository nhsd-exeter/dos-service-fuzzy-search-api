package uk.nhs.digital.uec.api.authentication.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import uk.nhs.digital.uec.api.authentication.exception.AccessTokenExpiredException;

public class JwtUtilTest {

  private static JwtUtil jwtUtil;
  private static String expiredAccessToken;
  private static String user;

  @BeforeAll
  public static void initialise() {
    jwtUtil = new JwtUtil();
    expiredAccessToken =
        "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJpZCIsImlhdCI6MTYzMjMyMjg1NCwic3ViIjoiYWRtaW5AbmhzLm5ldCIsImlzcyI6Imlzc3VlciIsImV4cCI6MTYzMjMyNjQ1NCwiY29nbml0bzpncm91cHMiOlsiRlVaWllfQVBJX0FDQ0VTUyIsIkFQSV9VU0VSIiwiUE9TVENPREVfQVBJX0FDQ0VTUyJdfQ.AiD4_0DgTq9Osv8Vh7z5SYXayVkQfBTyM_p6_sMQvp9zVy-aOMBhDuL4cZAz44YRYYeF1XP2hVtVAP8joIKis-_hgoMpFk2eDV9k1vCoM_ORsmO5bvtMwhgJr_feJ5El3sn8rj1Op4L-vBityjog_M8GTdX74CB2mk5N8vZMcsURnGFyHRe7Hak-68sWBFKUO9phy61BY2r-4N-tvdX6rEqUXnEWlGLUH0YtHdwdhy_gFP9Dd1ml9XxHauQI_Ycr7-LuYKNQ2P1BpT7SNc80h4mds5epI20nhu8mdJikO7iyfFdIxbQ-i3ZNNgAiVyOmy-hYeXPC-UszUFhu3NVv6g";
    user = "admin@nhs.net";
  }

  @Test
  public void expiredTokenTest() {
    AccessTokenExpiredException accessTokenExpiredException =
        assertThrows(
            AccessTokenExpiredException.class, () -> jwtUtil.isTokenValid(expiredAccessToken));
    assertNotNull(accessTokenExpiredException);
  }

  @Test
  public void invalidTokenTest() {
    String accessToken = "Invalid-ACCESS-ToKeN";
    IllegalStateException illegalStateException =
        assertThrows(IllegalStateException.class, () -> jwtUtil.isTokenValid(accessToken));
    assertNotNull(illegalStateException);
  }

  @Test
  public void getUserNameFromTokenTest() {
    String userNameFromToken = jwtUtil.getUserNameFromToken(expiredAccessToken);
    assertEquals(user, userNameFromToken);
  }

  @Test
  public void getTokenFromHeaderTest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + expiredAccessToken);
    String accessToken = jwtUtil.getTokenFromHeader(request);
    assertEquals(accessToken, expiredAccessToken);
  }

  @Test
  public void getIdentityProviderIdTest() {
    String identityProviderIdDigest = jwtUtil.getIdentityProviderIdDigest(user);
    String hexValue = "ebde84c8271f499e218354cc9024b65732f417fb";
    assertEquals(hexValue, identityProviderIdDigest);
  }
}

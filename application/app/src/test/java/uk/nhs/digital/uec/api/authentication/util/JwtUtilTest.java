package uk.nhs.digital.uec.api.authentication.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class JwtUtilTest {

  private JwtUtil jwtUtil;

  @BeforeEach
  public void setUp() {
    jwtUtil = new JwtUtil();
  }

  @Test
  public void resetAuthorizationHeaderTest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    String newAccessToken = "newly-Generated-AccEss-Token-from-Refresh_token";
    HttpServletRequest requestModified = jwtUtil.resetAuthorizationHeader(request, newAccessToken);
    String actual = requestModified.getHeader("Authorization");
    String expected = "Bearer " + newAccessToken;
    assertEquals(expected, actual);
  }
}

package uk.nhs.digital.uec.api.authentication.filter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.authentication.exception.AccessTokenExpiredException;
import uk.nhs.digital.uec.api.authentication.util.JwtUtil;

@ExtendWith(SpringExtension.class)
public class AccessTokenFilterTest {

  @Mock private HttpServletRequest httpRequest;

  @Mock private JwtUtil jwtUtil;

  @Mock private Authentication authentication;
  @Mock private SecurityContext securityContext;
  @Mock private HttpServletResponse httpResponse;
  @Mock private FilterChain filterChain;

  @InjectMocks private AccessTokenFilter filter = new AccessTokenFilter();
  private String token;

  @BeforeEach
  public void init() {
    token =
        "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJpZCIsImlhdCI6MTYyNjc3NTgyMywic3ViIjoiYWRtaW5AbmhzLm5ldCIsImlzcyI6Imlzc3VlciIsImV4cCI6MTYyNjc3OTQyMywiY29nbml0bzpncm91cHMiOlsiQVBJX1VTRVIiXX0.b1Q8Fc8lqiQuHO9tvjQW05MSOdvJ2hy33r-IO5VNUXP-zIwbA7yxl2WMvsoCxKn03CRoDhBQsJlaOhLCtsV_xwfKoLL3hKMZQ_CLQskuBrj4Xus9WmXyrqKyWAUoVAQ3O5NWtWuo8OYmEEcCLdFerv6lKRorIkb_U5ojB0xN1nkEZve0rAMXpDdhMpzNt6e3C_vrtasPAYuvx628gb9Bf9vixA-XMi4xFu2V5N9kKXFRA3w-iHDFUq3kOGtRLLpPRkOOoGo7bEeJGZ_JK2zc4uD-CIJVHIb8Ehf19c9NLk0fnuezWQkXxnbC-sjulJJqGUj_8uHrGM5w_xKWilatEg";
  }

  @Test
  public void authorisationFilterTest()
      throws ServletException, IOException, AccessTokenExpiredException {
    when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    filter.doFilterInternal(httpRequest, httpResponse, filterChain);
    verify(filterChain).doFilter(httpRequest, httpResponse);
  }
}

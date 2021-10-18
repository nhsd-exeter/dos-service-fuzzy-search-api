package uk.nhs.digital.uec.api.authentication.filter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
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
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.service.AuthenticationService;
import uk.nhs.digital.uec.api.authentication.util.JwtUtil;

@ExtendWith(SpringExtension.class)
public class AuthenticationTokenFilterTest {

  @Mock private HttpServletRequest request;
  @Mock private SecurityContext securityContext;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;
  @Mock private Authentication authentication;

  @InjectMocks private AccessTokenFilter accessTokenFilter;
  @InjectMocks private RefreshTokenFilter refreshTokenFilter;
  @Mock private AuthenticationService authenticationService;
  @Mock private JwtUtil jwtUtil;
  private String accessToken;

  @BeforeEach
  public void init() {
    SecurityContextHolder.setContext(securityContext);
    accessToken =
        "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJpZCIsImlhdCI6MTYyNjc3NTgyMywic3ViIjoiYWRtaW5AbmhzLm5ldCIsImlzcyI6Imlzc3VlciIsImV4cCI6MTYyNjc3OTQyMywiY29nbml0bzpncm91cHMiOlsiQVBJX1VTRVIiXX0.b1Q8Fc8lqiQuHO9tvjQW05MSOdvJ2hy33r-IO5VNUXP-zIwbA7yxl2WMvsoCxKn03CRoDhBQsJlaOhLCtsV_xwfKoLL3hKMZQ_CLQskuBrj4Xus9WmXyrqKyWAUoVAQ3O5NWtWuo8OYmEEcCLdFerv6lKRorIkb_U5ojB0xN1nkEZve0rAMXpDdhMpzNt6e3C_vrtasPAYuvx628gb9Bf9vixA-XMi4xFu2V5N9kKXFRA3w-iHDFUq3kOGtRLLpPRkOOoGo7bEeJGZ_JK2zc4uD-CIJVHIb8Ehf19c9NLk0fnuezWQkXxnbC-sjulJJqGUj_8uHrGM5w_xKWilatEg";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(request.getHeader("REFRESH_TOKEN")).thenReturn("Bearer " + accessToken);
  }

  @Test
  public void authorisationFilterTest()
      throws ServletException, IOException, AccessTokenExpiredException {
    accessTokenFilter.doFilterInternal(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void refreshTokenFilterTest()
      throws ServletException, IOException, AccessTokenExpiredException {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    refreshTokenFilter.doFilterInternal(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void tokenExpiredTest() throws ServletException, IOException, AccessTokenExpiredException {
    doThrow(new AccessTokenExpiredException()).when(jwtUtil).isTokenValid(any());
    assertThrows(
        Exception.class, () -> refreshTokenFilter.doFilterInternal(request, response, filterChain));
  }

  @Test
  public void refreshTokenFilterHeaderResetTest()
      throws ServletException, IOException, AccessTokenExpiredException {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    refreshTokenFilter.doFilterInternal(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void refreshTokenNullResponseTest()
      throws ServletException, IOException, AccessTokenExpiredException {
    AuthToken authToken = new AuthToken();
    authToken.setAccessToken("GeNaRated-AccEss-tOkEn");
    authToken.setRefreshToken(null);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authenticationService.refreshToken(anyString(), anyString())).thenReturn(authToken);
    assertThrows(
        IllegalStateException.class,
        () -> refreshTokenFilter.refresh(request, accessToken, "GeNeRated-reFresh-Token"));
  }

  @Test
  public void refreshAccessTokenNullResponseTest()
      throws ServletException, IOException, AccessTokenExpiredException {
    AuthToken authToken = new AuthToken();
    authToken.setAccessToken(null);
    authToken.setRefreshToken("GeNeRated-reFresh-Token");
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authenticationService.refreshToken(anyString(), anyString())).thenReturn(authToken);
    assertThrows(
        IllegalStateException.class,
        () -> refreshTokenFilter.refresh(request, accessToken, "GeNeRated-reFresh-Token"));
  }

  @Test
  public void refreshAccessTokenObjectNullResponseTest()
      throws ServletException, IOException, AccessTokenExpiredException {
    AuthToken authToken = null;
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authenticationService.refreshToken(anyString(), anyString())).thenReturn(authToken);
    assertThrows(
        IllegalStateException.class,
        () -> refreshTokenFilter.refresh(request, accessToken, "GeNeRated-reFresh-Token"));
  }
}

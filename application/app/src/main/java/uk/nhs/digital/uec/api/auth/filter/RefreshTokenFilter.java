package uk.nhs.digital.uec.api.auth.filter;

import static uk.nhs.digital.uec.api.auth.AuthConstants.ACCESS_TOKEN;
import static uk.nhs.digital.uec.api.auth.AuthConstants.REFRESH_TOKEN;
import static uk.nhs.digital.uec.api.auth.AuthConstants.SUB;
import static uk.nhs.digital.uec.api.auth.util.CookieUtil.getCookieValue;

import com.auth0.jwt.JWT;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.nhs.digital.uec.api.auth.exception.AccessTokenExpiredException;
import uk.nhs.digital.uec.api.auth.factory.CookieFactory;

/**
 * A filter responsible for replacing expired access tokens with fresh ones. These tokens are held
 * in Cookies.
 */
@Slf4j
public class RefreshTokenFilter extends OncePerRequestFilter {

  private final AccessTokenChecker accessTokenChecker;

  private final RefreshTokenService refreshTokenService;

  private final CookieFactory cookieFactory;

  public RefreshTokenFilter(
      RefreshTokenService refreshTokenService,
      AccessTokenChecker accessTokenChecker,
      CookieFactory cookieFactory) {
    this.refreshTokenService = refreshTokenService;
    this.accessTokenChecker = accessTokenChecker;
    this.cookieFactory = cookieFactory;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String accessToken = getCookieValue(request, ACCESS_TOKEN);
    String refreshToken = getCookieValue(request, REFRESH_TOKEN);
    try {
      try {
        accessTokenChecker.isValid(accessToken);
      } catch (AccessTokenExpiredException e) {
        String identityProviderId = getSubFromAccessToken(accessToken);
        // TODO: Refresh has to return access and refresh token and reset cookie
        refreshTokenService.refresh(refreshToken, identityProviderId);
        request = resetCookies(request, response, accessToken, refreshToken);
      }
    } catch (IllegalStateException | IllegalArgumentException | RestClientException e) {
      request = resetCookies(request, response, null, null);
    }
    chain.doFilter(request, response);
  }

  private HttpServletRequest resetCookies(
      HttpServletRequest request,
      HttpServletResponse response,
      String newAccessToken,
      String newRefreshToken) {
    CookieRequestWrapper cookieRequestWrapper = new CookieRequestWrapper(request, cookieFactory);
    cookieRequestWrapper.replaceTokenHeaders(newAccessToken, newRefreshToken);
    response.addCookie(cookieFactory.createAccessToken(newAccessToken));
    response.addCookie(cookieFactory.createRefreshToken(newRefreshToken));
    return cookieRequestWrapper;
  }

  private String getSubFromAccessToken(String accessToken) {
    return JWT.decode(accessToken).getClaim(SUB).asString();
  }
}

package uk.nhs.digital.uec.api.authentication.filter;

import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.CLAIM_NAME;
import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.REFRESH_TOKEN;

import com.auth0.jwt.JWT;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.nhs.digital.uec.api.authentication.exception.AccessTokenExpiredException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.service.AuthenticationService;
import uk.nhs.digital.uec.api.authentication.util.JwtUtil;

@Component
public class RefreshTokenFilter extends OncePerRequestFilter {

  @Autowired private AuthenticationService authenticationService;
  @Autowired private JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String accessToken = jwtUtil.getTokenFromHeader(request);
    String refreshToken = request.getHeader(REFRESH_TOKEN);
    try {
      jwtUtil.isTokenValid(accessToken);
    } catch (AccessTokenExpiredException e) {
      if (StringUtils.isNotBlank(refreshToken)) {
        request = refresh(request, accessToken, refreshToken);
      }
    } catch (IllegalStateException | IllegalArgumentException | RestClientException e) {
      request = resetAuthorizationHeader(request, null);
    }
    chain.doFilter(request, response);
  }

  public HttpServletRequest refresh(
      HttpServletRequest request, String accessToken, String refreshToken) {
    AuthToken refreshedTokens =
        authenticationService.refreshToken(refreshToken, getSubFromAccessToken(accessToken));
    if (refreshedTokens == null
        || StringUtils.isBlank(refreshedTokens.getAccessToken())
        || StringUtils.isBlank(refreshedTokens.getRefreshToken())) {
      throw new IllegalStateException(
          "Unexpected state: null detected on accessToken / refreshToken");
    }
    return resetAuthorizationHeader(request, refreshedTokens.getAccessToken());
  }

  private HttpServletRequest resetAuthorizationHeader(
      HttpServletRequest request, String newAccessToken) {
    CustomHttpServletRequestWrapper httpReq = new CustomHttpServletRequestWrapper(request);
    httpReq.addHeader("Authorization", "Bearer " + newAccessToken);
    return httpReq;
  }

  private String getSubFromAccessToken(String accessToken) {
    return JWT.decode(accessToken).getClaim(CLAIM_NAME).asString();
  }
}

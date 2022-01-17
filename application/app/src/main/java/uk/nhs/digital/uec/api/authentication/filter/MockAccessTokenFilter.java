package uk.nhs.digital.uec.api.authentication.filter;

import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.ROLE_PREFIX;
import static uk.nhs.digital.uec.api.authentication.constants.MockAuthenticationConstants.MOCK_ACCESS_TOKEN;
import static uk.nhs.digital.uec.api.authentication.constants.MockAuthenticationConstants.MOCK_AUTH_GROUP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.nhs.digital.uec.api.authentication.exception.InvalidAccessToken;
import uk.nhs.digital.uec.api.authentication.util.JwtUtil;

@Component
@Profile("mock-auth")
@Slf4j
public class MockAccessTokenFilter extends OncePerRequestFilter {

  @Autowired private JwtUtil jwtUtil;

  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = jwtUtil.getTokenFromHeader(request);
    try {
      isMockTokenValid(token);
    } catch (InvalidAccessToken
        | IllegalStateException
        | IllegalArgumentException
        | RestClientException e) {
      log.error("Error occurred while validating access token", e.getMessage());
      token = null;
    }

    if (token != null) {
      Authentication origAuthentication = SecurityContextHolder.getContext().getAuthentication();
      Authentication newAuthentication = createNewAuthentication(origAuthentication);
      SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
    filterChain.doFilter(request, response);
  }

  private Authentication createNewAuthentication(Authentication origAuthentication) {
    List<String> groupList = Arrays.asList(MOCK_AUTH_GROUP);
    List<GrantedAuthority> authorities = convertCognitoGroupsToAuthorities(groupList);
    Object principal = origAuthentication != null ? origAuthentication.getPrincipal() : null;
    Object details = origAuthentication != null ? origAuthentication.getDetails() : null;
    return new UsernamePasswordAuthenticationToken(principal, details, authorities);
  }

  private List<GrantedAuthority> convertCognitoGroupsToAuthorities(List<String> groupList) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    groupList.forEach(
        cognitoGroupName -> {
          authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + cognitoGroupName));
        });
    return authorities;
  }

  private void isMockTokenValid(String accessToken) throws InvalidAccessToken {
    if (accessToken == null) return;
    if (!accessToken.equalsIgnoreCase(MOCK_ACCESS_TOKEN)) {
      log.info("Invalid access token");
      throw new InvalidAccessToken();
    }
  }
}

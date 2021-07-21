package uk.nhs.digital.uec.api.authentication.filter;

import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.COGNITO_GROUPS;
import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.IDENTITY_PROVIDER_ID;
import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.ROLE_PREFIX;
import static uk.nhs.digital.uec.api.authentication.constants.AuthenticationConstants.USER_HASH;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.nhs.digital.uec.api.authentication.exception.AccessTokenExpiredException;
import uk.nhs.digital.uec.api.authentication.util.JwtUtil;

@Component
@Slf4j
public class AccessTokenFilter extends OncePerRequestFilter {

  @Autowired private JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = jwtUtil.getTokenFromHeader(request);
    try {
      jwtUtil.isTokenValid(token);
    } catch (AccessTokenExpiredException
        | IllegalStateException
        | IllegalArgumentException
        | RestClientException e) {
      log.error("Error occurred while validating access token", e.getMessage());
      token = null;
    }

    if (token != null) {
      String userNameFromToken = jwtUtil.getUserNameFromToken(token);
      request.setAttribute(IDENTITY_PROVIDER_ID, userNameFromToken);
      request.setAttribute(USER_HASH, jwtUtil.getIdentityProviderIdDigest(userNameFromToken));
      Authentication origAuthentication = SecurityContextHolder.getContext().getAuthentication();
      Authentication newAuthentication = createNewAuthentication(origAuthentication, token);
      SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
    filterChain.doFilter(request, response);
  }

  private Authentication createNewAuthentication(
      Authentication origAuthentication, String accessToken) {
    List<String> groupList = getGroupsFromAccessToken(accessToken);
    List<GrantedAuthority> authorities = convertCognitoGroupsToAuthorities(groupList);
    Object principal = origAuthentication != null ? origAuthentication.getPrincipal() : null;
    Object details = origAuthentication != null ? origAuthentication.getDetails() : null;
    return new UsernamePasswordAuthenticationToken(principal, details, authorities);
  }

  private List<String> getGroupsFromAccessToken(String accessToken) {
    DecodedJWT jwt = JWT.decode(accessToken);
    Claim groupsClaim = jwt.getClaim(COGNITO_GROUPS);
    return groupsClaim == null || groupsClaim.asList(String.class) == null
        ? new ArrayList<>()
        : groupsClaim.asList(String.class);
  }

  private List<GrantedAuthority> convertCognitoGroupsToAuthorities(List<String> groupList) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    groupList.forEach(
        cognitoGroupName -> {
          authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + cognitoGroupName));
        });
    return authorities;
  }
}

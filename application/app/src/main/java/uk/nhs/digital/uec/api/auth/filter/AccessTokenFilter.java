package uk.nhs.digital.uec.api.auth.filter;

import static uk.nhs.digital.uec.api.auth.AuthConstants.ACCESS_TOKEN;
import static uk.nhs.digital.uec.api.auth.AuthConstants.COGNITO_GROUPS;
import static uk.nhs.digital.uec.api.auth.AuthConstants.IDENTITY_PROVIDER_ID;
import static uk.nhs.digital.uec.api.auth.AuthConstants.SUB;
import static uk.nhs.digital.uec.api.auth.AuthConstants.USER_HASH;
import static uk.nhs.digital.uec.api.auth.util.CookieUtil.getCookieValue;

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
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.nhs.digital.uec.api.auth.CookieTokenExtractor;
import uk.nhs.digital.uec.api.auth.request.FuzzySearchRequest;

/**
 * A web filter responsible for reading and processing the access token in the request. It adds
 * roles to the {@link Authentication} object previously set by the Spring {@code
 * OAuth2AuthenticationProcessingFilter}, and adds the identity provider ID to the request as a
 * request attribute. Cognito groups in the JWT are mapped directly to roles; the Cognito 'sub' is
 * used as the identity provider ID.
 */
public class AccessTokenFilter extends OncePerRequestFilter {

  private static final String ROLE_PREFIX = "ROLE_";

  @Autowired private FuzzySearchRequest fuzzySearchRequest;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String accessToken = getCookieValue(request, ACCESS_TOKEN);

    fuzzySearchRequest.setAuthenticationToken(
        CookieTokenExtractor.extractAuthenticationToken(request));

    if (accessToken != null) {
      String subFromAccessToken = getSubFromAccessToken(accessToken);
      request.setAttribute(IDENTITY_PROVIDER_ID, subFromAccessToken);
      request.setAttribute(USER_HASH, getIdentityProviderIdDigest(subFromAccessToken));
      Authentication origAuthentication = SecurityContextHolder.getContext().getAuthentication();
      if (origAuthentication != null) {
        Authentication newAuthentication = createNewAuthentication(origAuthentication, accessToken);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
      }
    }
    chain.doFilter(request, response);
  }

  private Authentication createNewAuthentication(
      Authentication origAuthentication, String accessToken) {
    List<String> groupList = getGroupsFromAccessToken(accessToken);
    List<GrantedAuthority> authorities = convertCognitoGroupsToAuthorities(groupList);
    return new UsernamePasswordAuthenticationToken(
        origAuthentication.getPrincipal(), origAuthentication.getDetails(), authorities);
  }

  private String getSubFromAccessToken(String accessToken) {
    return JWT.decode(accessToken).getClaim(SUB).asString();
  }

  private List<String> getGroupsFromAccessToken(String accessToken) {
    DecodedJWT jwt = JWT.decode(accessToken);
    Claim groupsClaim = jwt.getClaim(COGNITO_GROUPS);
    if (groupsClaim == null || groupsClaim.asList(String.class) == null) {
      return new ArrayList<>();
    }
    return groupsClaim.asList(String.class);
  }

  private List<GrantedAuthority> convertCognitoGroupsToAuthorities(List<String> groupList) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    groupList.forEach(
        cognitoGroupName -> {
          authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + cognitoGroupName));
        });
    return authorities;
  }

  private String getIdentityProviderIdDigest(String identityProviderId) {
    return DigestUtils.sha1Hex(identityProviderId);
  }
}

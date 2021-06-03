package uk.nhs.digital.uec.api.auth.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.nhs.digital.uec.api.auth.AuthConstants.ACCESS_TOKEN;
import static uk.nhs.digital.uec.api.auth.AuthConstants.IDENTITY_PROVIDER_ID;
import static uk.nhs.digital.uec.api.auth.AuthConstants.USER_HASH;
import static uk.nhs.digital.uec.api.auth.testsupport.TokenConstants.ACCESS_TOKEN_SUB;
import static uk.nhs.digital.uec.api.auth.testsupport.TokenConstants.ACCESS_TOKEN_WITH_SEARCH_GROUP;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.nhs.digital.uec.api.auth.request.FuzzySearchRequest;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenFilterTest {

  @InjectMocks private AccessTokenFilter accessTokenFilter;

  @Mock private FuzzySearchRequest fuzzySearchRequest;

  @Before
  public void setUp() {
    Authentication auth = new UsernamePasswordAuthenticationToken(null, null);
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  public void shouldAddRoleSearchAuthorityGivenCognitoSearchGroupClaim() throws Exception {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    FilterChain chain = mock(FilterChain.class);
    Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN, ACCESS_TOKEN_WITH_SEARCH_GROUP);
    when(request.getCookies()).thenReturn(new Cookie[] {accessTokenCookie});

    // when
    accessTokenFilter.doFilterInternal(request, response, chain);

    // then
    assertThat(
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().size(), is(1));
    assertThat(
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .findFirst()
            .get()
            .toString(),
        is("ROLE_SEARCH"));
  }

  @Test
  public void shouldAddIdentityProviderIdRequestAttributeGivenSubClaim() throws Exception {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);
    Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN, ACCESS_TOKEN_WITH_SEARCH_GROUP);
    when(request.getCookies()).thenReturn(new Cookie[] {accessTokenCookie});

    // when
    accessTokenFilter.doFilterInternal(request, response, chain);

    // then
    ArgumentCaptor<String> attributeNameCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Object> attributeValueCaptor = ArgumentCaptor.forClass(Object.class);
    verify(request, times(2))
        .setAttribute(attributeNameCaptor.capture(), attributeValueCaptor.capture());
    assertThat(attributeNameCaptor.getAllValues().get(0), is(IDENTITY_PROVIDER_ID));
    assertThat(attributeNameCaptor.getAllValues().get(1), is(USER_HASH));
    assertThat(attributeValueCaptor.getAllValues().get(0), is(ACCESS_TOKEN_SUB));
  }
}

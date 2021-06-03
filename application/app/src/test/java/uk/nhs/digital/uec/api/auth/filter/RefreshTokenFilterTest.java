package uk.nhs.digital.uec.api.auth.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.nhs.digital.uec.api.auth.AuthConstants.ACCESS_TOKEN;
import static uk.nhs.digital.uec.api.auth.AuthConstants.REFRESH_TOKEN;
import static uk.nhs.digital.uec.api.auth.testsupport.CookieMatcher.cookieMatching;
import static uk.nhs.digital.uec.api.auth.testsupport.TokenConstants.ACCESS_TOKEN_SUB;
import static uk.nhs.digital.uec.api.auth.testsupport.TokenConstants.ACCESS_TOKEN_WITH_SEARCH_GROUP;

import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.RestClientException;
import uk.nhs.digital.uec.api.auth.exception.AccessTokenExpiredException;
import uk.nhs.digital.uec.api.auth.factory.CookieFactory;
import uk.nhs.digital.uec.api.auth.model.LoginResult;

/** Tests for {@link RefreshTokenFilter} */
@RunWith(MockitoJUnitRunner.Silent.class)
public class RefreshTokenFilterTest {

  private static final String EXPIRED_COOKIE_HEADER_FORMAT =
      "%s=; Path=/; Domain=localhost; Max-Age=0; "
          + "Expires=Thu, 01 Jan 1970 00:00:00 GMT; Secure; HttpOnly";

  private static final String EXPIRED_ACCESS_TOKEN_HEADER_STRING =
      String.format(EXPIRED_COOKIE_HEADER_FORMAT, "ACCESS_TOKEN");

  private static final String EXPIRED_REFRESH_TOKEN_HEADER_STRING =
      String.format(EXPIRED_COOKIE_HEADER_FORMAT, "REFRESH_TOKEN");

  private static final String ORIGINAL_REFRESH_TOKEN = "ORIGINAL_REFRESH_TOKEN";

  private static final String NEW_ACCESS_TOKEN = "NEW_ACCESS_TOKEN";

  private static final String NEW_REFRESH_TOKEN = "NEW_REFRESH_TOKEN";

  @Mock private RefreshTokenService refreshTokenService;

  @Mock private AccessTokenChecker accessTokenChecker;

  private FilterChain filterChain;

  private RefreshTokenFilter filter;

  private MockHttpServletRequest request;

  private MockHttpServletResponse response;

  @Before
  public void setup() {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    filterChain = mock(FilterChain.class);
    CookieFactory cookieFactory = new CookieFactory("localhost");
    filter = new RefreshTokenFilter(refreshTokenService, accessTokenChecker, cookieFactory);
  }

  @Test
  public void shouldUseFreshTokensGivenTokensPresentAndAccessTokenExpired()
      throws ServletException, IOException, AccessTokenExpiredException {
    // Given
    request.setCookies(
        new Cookie(ACCESS_TOKEN, ACCESS_TOKEN_WITH_SEARCH_GROUP),
        new Cookie(REFRESH_TOKEN, ORIGINAL_REFRESH_TOKEN));
    doThrow(new AccessTokenExpiredException())
        .when(accessTokenChecker)
        .isValid(ACCESS_TOKEN_WITH_SEARCH_GROUP);
    LoginResult loginResult = new LoginResult(NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
    when(refreshTokenService.refresh(ORIGINAL_REFRESH_TOKEN, ACCESS_TOKEN_SUB))
        .thenReturn(loginResult);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    ArgumentCaptor<MockHttpServletResponse> responseCaptor =
        ArgumentCaptor.forClass(MockHttpServletResponse.class);
    verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());

    Cookie[] requestCookies = requestCaptor.getValue().getCookies();
    assertThat(2, is(requestCookies.length));
    Cookie expectedRequestAccessCookie = getExpectedCookie(ACCESS_TOKEN, NEW_ACCESS_TOKEN);
    assertThat(requestCookies[0], is(cookieMatching(expectedRequestAccessCookie)));
    Cookie expectedRequestRefreshCookie = getExpectedCookie(REFRESH_TOKEN, NEW_REFRESH_TOKEN);
    assertThat(requestCookies[1], is(cookieMatching(expectedRequestRefreshCookie)));

    Cookie[] responseCookies = responseCaptor.getValue().getCookies();
    assertThat(2, is(responseCookies.length));
    Cookie expectedResponseAccessCookie = getExpectedCookie(ACCESS_TOKEN, NEW_ACCESS_TOKEN);
    assertThat(responseCookies[0], is(cookieMatching(expectedResponseAccessCookie)));
    Cookie expectedResponseRefreshCookie = getExpectedCookie(REFRESH_TOKEN, NEW_REFRESH_TOKEN);
    assertThat(responseCookies[1], is(cookieMatching(expectedResponseRefreshCookie)));
  }

  @Test
  public void shouldClearTokensGivenTokensPresentAndAccessTokenInvalid()
      throws ServletException, IOException, AccessTokenExpiredException {
    // Given
    String invalidAccessTokenValue = "invalid.access.token.value";
    request.setCookies(
        new Cookie(ACCESS_TOKEN, invalidAccessTokenValue),
        new Cookie(REFRESH_TOKEN, ORIGINAL_REFRESH_TOKEN));
    doThrow(new IllegalStateException()).when(accessTokenChecker).isValid(invalidAccessTokenValue);
    LoginResult loginResult = new LoginResult(NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
    when(refreshTokenService.refresh(ORIGINAL_REFRESH_TOKEN, ACCESS_TOKEN_SUB))
        .thenReturn(loginResult);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    ArgumentCaptor<MockHttpServletResponse> responseCaptor =
        ArgumentCaptor.forClass(MockHttpServletResponse.class);
    verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());

    Cookie[] requestCookies = requestCaptor.getValue().getCookies();
    assertThat(0, is(requestCookies.length));

    List<String> responseCookies = responseCaptor.getValue().getHeaders(HttpHeaders.SET_COOKIE);
    assertThat(2, is(responseCookies.size()));
    assertThat(responseCookies.get(0), is(EXPIRED_ACCESS_TOKEN_HEADER_STRING));
    assertThat(responseCookies.get(1), is(EXPIRED_REFRESH_TOKEN_HEADER_STRING));
  }

  @Test
  public void shouldClearTokensGivenAccessTokenCannotBeDecoded()
      throws ServletException, IOException, AccessTokenExpiredException {
    // Given
    request.setCookies(
        new Cookie(ACCESS_TOKEN, ACCESS_TOKEN_WITH_SEARCH_GROUP),
        new Cookie(REFRESH_TOKEN, ORIGINAL_REFRESH_TOKEN));
    doThrow(new AccessTokenExpiredException())
        .when(accessTokenChecker)
        .isValid(ACCESS_TOKEN_WITH_SEARCH_GROUP);
    LoginResult loginResult = new LoginResult(NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
    when(refreshTokenService.refresh(ORIGINAL_REFRESH_TOKEN, ACCESS_TOKEN_SUB))
        .thenReturn(loginResult);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    ArgumentCaptor<MockHttpServletResponse> responseCaptor =
        ArgumentCaptor.forClass(MockHttpServletResponse.class);
    verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());

    Cookie[] requestCookies = requestCaptor.getValue().getCookies();
    assertThat(2, is(requestCookies.length));
    Cookie expectedRequestAccessCookie = getExpectedCookie(ACCESS_TOKEN, NEW_ACCESS_TOKEN);
    assertThat(requestCookies[0], is(cookieMatching(expectedRequestAccessCookie)));
    Cookie expectedRequestRefreshCookie = getExpectedCookie(REFRESH_TOKEN, NEW_REFRESH_TOKEN);
    assertThat(requestCookies[1], is(cookieMatching(expectedRequestRefreshCookie)));

    Cookie[] responseCookies = responseCaptor.getValue().getCookies();
    assertThat(2, is(responseCookies.length));
    Cookie expectedResponseAccessCookie = getExpectedCookie(ACCESS_TOKEN, NEW_ACCESS_TOKEN);
    assertThat(responseCookies[0], is(cookieMatching(expectedResponseAccessCookie)));
    Cookie expectedResponseRefreshCookie = getExpectedCookie(REFRESH_TOKEN, NEW_REFRESH_TOKEN);
    assertThat(responseCookies[1], is(cookieMatching(expectedResponseRefreshCookie)));
  }

  @Test
  public void shouldClearTokensGivenRefreshTokenPresentAndAccessTokenMissing()
      throws ServletException, IOException, AccessTokenExpiredException {
    // Given
    request.setCookies(new Cookie(REFRESH_TOKEN, ORIGINAL_REFRESH_TOKEN));
    doThrow(new IllegalArgumentException()).when(accessTokenChecker).isValid(any());
    LoginResult loginResult = new LoginResult(NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
    when(refreshTokenService.refresh(ORIGINAL_REFRESH_TOKEN, ACCESS_TOKEN_SUB))
        .thenReturn(loginResult);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(accessTokenChecker, never()).isValid(anyString());
    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    ArgumentCaptor<MockHttpServletResponse> responseCaptor =
        ArgumentCaptor.forClass(MockHttpServletResponse.class);
    verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());

    Cookie[] requestCookies = requestCaptor.getValue().getCookies();
    assertThat(0, is(requestCookies.length));

    List<String> responseCookies = responseCaptor.getValue().getHeaders(HttpHeaders.SET_COOKIE);
    assertThat(2, is(responseCookies.size()));
    assertThat(responseCookies.get(0), is(EXPIRED_ACCESS_TOKEN_HEADER_STRING));
    assertThat(responseCookies.get(1), is(EXPIRED_REFRESH_TOKEN_HEADER_STRING));
  }

  @Test
  public void shouldClearTokensGivenRefreshServiceThrowsRestClientException()
      throws ServletException, IOException, AccessTokenExpiredException {
    // Given
    request.setCookies(
        new Cookie(ACCESS_TOKEN, ACCESS_TOKEN_WITH_SEARCH_GROUP),
        new Cookie(REFRESH_TOKEN, ORIGINAL_REFRESH_TOKEN));
    doThrow(new AccessTokenExpiredException())
        .when(accessTokenChecker)
        .isValid(ACCESS_TOKEN_WITH_SEARCH_GROUP);
    doThrow(new RestClientException("fail"))
        .when(refreshTokenService)
        .refresh(ORIGINAL_REFRESH_TOKEN, ACCESS_TOKEN_SUB);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    ArgumentCaptor<MockHttpServletResponse> responseCaptor =
        ArgumentCaptor.forClass(MockHttpServletResponse.class);
    verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());

    Cookie[] requestCookies = requestCaptor.getValue().getCookies();
    assertThat(0, is(requestCookies.length));

    List<String> responseCookies = responseCaptor.getValue().getHeaders(HttpHeaders.SET_COOKIE);
    assertThat(2, is(responseCookies.size()));
    assertThat(responseCookies.get(0), is(EXPIRED_ACCESS_TOKEN_HEADER_STRING));
    assertThat(responseCookies.get(1), is(EXPIRED_REFRESH_TOKEN_HEADER_STRING));
  }

  @Test
  public void shouldClearTokensGivenRefreshServiceReturnsNullRefreshToken()
      throws ServletException, IOException, AccessTokenExpiredException {
    // Given
    request.setCookies(
        new Cookie(ACCESS_TOKEN, ACCESS_TOKEN_WITH_SEARCH_GROUP),
        new Cookie(REFRESH_TOKEN, ORIGINAL_REFRESH_TOKEN));
    doThrow(new AccessTokenExpiredException())
        .when(accessTokenChecker)
        .isValid(ACCESS_TOKEN_WITH_SEARCH_GROUP);
    doThrow(new IllegalStateException()).when(refreshTokenService).refresh(any(), any());

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    ArgumentCaptor<MockHttpServletResponse> responseCaptor =
        ArgumentCaptor.forClass(MockHttpServletResponse.class);
    verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());

    Cookie[] requestCookies = requestCaptor.getValue().getCookies();
    assertThat(0, is(requestCookies.length));

    List<String> responseCookies = responseCaptor.getValue().getHeaders(HttpHeaders.SET_COOKIE);
    assertThat(2, is(responseCookies.size()));
    assertThat(responseCookies.get(0), is(EXPIRED_ACCESS_TOKEN_HEADER_STRING));
    assertThat(responseCookies.get(1), is(EXPIRED_REFRESH_TOKEN_HEADER_STRING));
  }

  @Test
  public void shouldUseOriginalTokensGivenTokensPresentAndAccessTokenNotExpired()
      throws ServletException, IOException {
    // Given
    request.setCookies(
        new Cookie(ACCESS_TOKEN, ACCESS_TOKEN_WITH_SEARCH_GROUP),
        new Cookie(REFRESH_TOKEN, ORIGINAL_REFRESH_TOKEN));

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(refreshTokenService, never()).refresh(anyString(), anyString());
    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    ArgumentCaptor<MockHttpServletResponse> responseCaptor =
        ArgumentCaptor.forClass(MockHttpServletResponse.class);
    verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());

    Cookie[] requestCookies = requestCaptor.getValue().getCookies();
    assertThat(2, is(requestCookies.length));
    assertThat(requestCookies[0].getName(), is(ACCESS_TOKEN));
    assertThat(requestCookies[0].getValue(), is(ACCESS_TOKEN_WITH_SEARCH_GROUP));
    assertThat(requestCookies[1].getName(), is(REFRESH_TOKEN));
    assertThat(requestCookies[1].getValue(), is(ORIGINAL_REFRESH_TOKEN));

    List<String> responseCookies = responseCaptor.getValue().getHeaders(HttpHeaders.SET_COOKIE);
    assertThat(responseCookies, is(empty()));
  }

  @Test
  public void shouldUseOriginalTokenGivenAccessTokenPresentAndNotExpired()
      throws ServletException, IOException {
    // Given
    request.setCookies(new Cookie(ACCESS_TOKEN, ACCESS_TOKEN_WITH_SEARCH_GROUP));

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(refreshTokenService, never()).refresh(anyString(), anyString());
    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    ArgumentCaptor<MockHttpServletResponse> responseCaptor =
        ArgumentCaptor.forClass(MockHttpServletResponse.class);
    verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());

    Cookie[] requestCookies = requestCaptor.getValue().getCookies();
    assertThat(1, is(requestCookies.length));
    assertThat(requestCookies[0].getName(), is(ACCESS_TOKEN));
    assertThat(requestCookies[0].getValue(), is(ACCESS_TOKEN_WITH_SEARCH_GROUP));

    List<String> responseCookies = responseCaptor.getValue().getHeaders(HttpHeaders.SET_COOKIE);
    assertThat(responseCookies, is(empty()));
  }

  @Test
  public void shouldNotAttemptToCheckTokensGivenNoTokensPresent()
      throws ServletException, IOException, AccessTokenExpiredException {
    // Given
    request.setCookies();

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(accessTokenChecker, never()).isValid(anyString());
    verify(refreshTokenService, never()).refresh(anyString(), anyString());
  }

  private Cookie getExpectedCookie(String cookieName, String cookieValue) {
    Cookie expectedCookie = new Cookie(cookieName, cookieValue);
    expectedCookie.setPath("/");
    expectedCookie.setSecure(true);
    expectedCookie.setHttpOnly(true);
    expectedCookie.setMaxAge(86400);
    expectedCookie.setDomain("localhost");
    return expectedCookie;
  }
}

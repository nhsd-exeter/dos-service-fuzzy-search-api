package uk.nhs.digital.uec.api.auth.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.nhs.digital.uec.api.auth.filter.RefreshTokenService.REFRESH_PATH;

import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;
import uk.nhs.digital.uec.api.auth.model.AuthTokens;
import uk.nhs.digital.uec.api.auth.model.RefreshTokens;

@RunWith(JUnitParamsRunner.class)
public class RefreshTokenServiceTest {

  private static final String USER_MANAGEMENT_URL = "https://test-um-url";

  private RestTemplate restTemplate;

  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  private RefreshTokenService service;

  @Before
  public void setUp() {
    restTemplate = mock(RestTemplate.class);
    service = new RefreshTokenService(restTemplate);
  }

  @Test
  public void shouldPostToCorrectUrlWithAccessTokenInPayload() {
    // Given
    String accessToken = "access.token.value";
    AuthTokens loginResultFromRestTemplate =
        new AuthTokens(accessToken, "refresh.token.test.value");
    when(restTemplate.postForObject(anyString(), any(), eq(AuthTokens.class)))
        .thenReturn(loginResultFromRestTemplate);

    // When
    // AuthTokens loginResult = service.refresh(accessToken, "sub");

    // Then
    // assertEquals(loginResult, loginResultFromRestTemplate);
    ArgumentCaptor<RefreshTokens> refreshTokensCaptor =
        ArgumentCaptor.forClass(RefreshTokens.class);
    ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(restTemplate)
        .postForObject(urlCaptor.capture(), refreshTokensCaptor.capture(), eq(AuthTokens.class));
    assertThat(urlCaptor.getValue(), is(USER_MANAGEMENT_URL + REFRESH_PATH));
    assertThat(refreshTokensCaptor.getValue().getRefreshToken(), is(accessToken));
  }

  @Test
  public void postShouldFailGivenNullLoginResult() {
    // Given
    when(restTemplate.postForObject(anyString(), any(), eq(AuthTokens.class))).thenReturn(null);

    // Expectations
    exceptionRule.expect(IllegalStateException.class);
    exceptionRule.expectMessage(
        "Unexpected state: null detected on loginResult / accessToken / refreshToken");

    // When
    service.refresh("access.token.value", "sub");
  }

  @Test
  public void shouldFailToConstructGivenNullRestTemplate() {
    // Given
    RestTemplate restTemplate = null;

    // Expectations
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("restTemplate must not be null");

    // When
    new RefreshTokenService(restTemplate);
  }

  @Test
  public void shouldFailToConstructGivenNullUserManagementUrl() {
    // Given
    String userManagementUrl = null;

    // Expectations
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("userManagementUrl must have text");

    // When
    new RefreshTokenService(mock(RestTemplate.class));
  }

  @Test
  public void shouldFailToConstructGivenEmptyUserManagementUrl() {
    // Given
    String userManagementUrl = "";

    // Expectations
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("userManagementUrl must have text");

    // When
    new RefreshTokenService(mock(RestTemplate.class));
  }

  private Object[] loginResultParametersToTest() {
    return new Object[] {
      new Object[] {
        null,
        IllegalStateException.class,
        "Unexpected state: null detected on loginResult / accessToken / refreshToken"
      },
      new Object[] {
        new AuthTokens(),
        IllegalStateException.class,
        "Unexpected state: null detected on loginResult / accessToken / refreshToken"
      },
      new Object[] {
        new AuthTokens("access.token.value", null),
        IllegalStateException.class,
        "Unexpected state: null detected on loginResult / accessToken / refreshToken"
      },
      new Object[] {
        new AuthTokens("access.token.value", ""),
        IllegalStateException.class,
        "Unexpected state: null detected on loginResult / accessToken / refreshToken"
      },
      new Object[] {
        new AuthTokens(null, "refresh.token.test.value"),
        IllegalStateException.class,
        "Unexpected state: null detected on loginResult / accessToken / refreshToken"
      },
      new Object[] {
        new AuthTokens("", "refresh.token.test.value"),
        IllegalStateException.class,
        "Unexpected state: null detected on loginResult / accessToken / refreshToken"
      }
    };
  }

  private Object[] refreshParametersToTest() {
    return new Object[] {
      new Object[] {null, "sub", IllegalArgumentException.class, "refreshToken must have text"},
      new Object[] {"", "sub", IllegalArgumentException.class, "refreshToken must have text"},
      new Object[] {
        "refresh.token.test.value",
        null,
        IllegalArgumentException.class,
        "identityProviderId must have text"
      },
      new Object[] {
        "refresh.token.test.value",
        "",
        IllegalArgumentException.class,
        "identityProviderId must have text"
      }
    };
  }
}

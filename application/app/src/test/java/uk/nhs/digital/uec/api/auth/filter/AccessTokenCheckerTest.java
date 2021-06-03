package uk.nhs.digital.uec.api.auth.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.digital.uec.api.auth.exception.AccessTokenExpiredException;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenCheckerTest {

  @Mock private JwtDecoder decoder;

  @Mock private DecodedJWT jwt;

  @InjectMocks private AccessTokenChecker accessTokenChecker;

  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void shouldThrowAccessTokenExpiredExceptionWhenAccessTokenIsExpired() {

    // given
    when(decoder.decode(anyString())).thenReturn(jwt);
    when(jwt.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() - 99999));

    // when
    Exception thrownException = null;
    try {
      accessTokenChecker.isValid("abc");
    } catch (Exception e) {
      thrownException = e;
    }

    // then
    assertThat(thrownException).isInstanceOf(AccessTokenExpiredException.class);
  }

  @Test
  public void shouldDoNothingWhenAccessTokenIsNotExpired() {

    // given
    when(decoder.decode(anyString())).thenReturn(jwt);
    when(jwt.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 99999));

    // when
    Exception thrownException = null;
    try {
      accessTokenChecker.isValid("abc");
    } catch (Exception e) {
      thrownException = e;
    }

    // then
    assertThat(thrownException).isNull();
  }

  @Test
  public void shouldThrowIllegalStateExceptionWhenJwtCannotBeDecoded() {

    // given
    when(decoder.decode(anyString())).thenThrow(new JWTDecodeException("dummy-message"));

    // when
    Exception thrownException = null;
    try {
      accessTokenChecker.isValid("abc");
    } catch (Exception e) {
      thrownException = e;
    }

    // then
    assertThat(thrownException).isInstanceOf(IllegalStateException.class);
  }

  @Test
  public void shouldFailGivenNullAccessToken() throws AccessTokenExpiredException {
    // Given

    // Expectations
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("accessToken must have text");

    // When
    accessTokenChecker.isValid(null);
  }

  @Test
  public void shouldFailGivenEmptyAccessToken() throws AccessTokenExpiredException {
    // Given

    // Expectations
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("accessToken must have text");

    // When
    accessTokenChecker.isValid("");
  }
}

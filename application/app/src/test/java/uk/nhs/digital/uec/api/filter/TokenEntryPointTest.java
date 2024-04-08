package uk.nhs.digital.uec.api.filter;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.AuthenticationException;

import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenEntryPointTest {

  TokenEntryPoint tokenEntryPoint = new TokenEntryPoint();

  @Test
  public void testTokenEntryPoint() throws IOException {
    HttpServletResponse response = mock(HttpServletResponse.class);
    AuthenticationException authException = new AccountExpiredException("Test Exception, Account Expired");
    tokenEntryPoint.commence(null, response, authException);
    verify(response, times(1)).sendError(eq(HttpStatus.SC_UNAUTHORIZED), eq("Test Exception, Account Expired"));
  }

}

package uk.nhs.digital.uec.api.authentication.filter;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class TokenEntryPoint implements AuthenticationEntryPoint {

  @Value("${token.expiration.message}")
  private String expirationMessage;

  /**
   * This exception class is used to catch the spring filter exception during Authorisation and
   * Access Token validation
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    response.sendError(HttpStatus.SC_UNAUTHORIZED, expirationMessage);
  }
}

package uk.nhs.digital.uec.api.filter;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class TokenEntryPoint implements AuthenticationEntryPoint {

  /** This exception class handles the Authentication Exceptions */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    response.sendError(HttpStatus.SC_UNAUTHORIZED, authException.getMessage());
  }
}

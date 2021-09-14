package uk.nhs.digital.uec.api.filter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Component
public class TokenEntryPoint implements AuthenticationEntryPoint {

  /** This exception class handles the Authentication Exceptions */
  @Override
  public void commence(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException authException)
    throws IOException {
    response.sendError(SC_UNAUTHORIZED, authException.getMessage());
  }
}

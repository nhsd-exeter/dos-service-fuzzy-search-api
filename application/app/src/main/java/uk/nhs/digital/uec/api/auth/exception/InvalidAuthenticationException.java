package uk.nhs.digital.uec.api.auth.exception;

/** {@link Exception} thrown when an invalid login exception occurs when calling the Cognito API. */
public class InvalidAuthenticationException extends RuntimeException {

  public InvalidAuthenticationException(String message) {
    super(message);
  }
}

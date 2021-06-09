package uk.nhs.digital.uec.api.auth.exception;

/** {@link Exception} thrown when an invalid login exception occurs when calling the Cognito API. */
public class InvalidAccessTokenException extends RuntimeException {

  public InvalidAccessTokenException(String message) {
    super(message);
  }
}

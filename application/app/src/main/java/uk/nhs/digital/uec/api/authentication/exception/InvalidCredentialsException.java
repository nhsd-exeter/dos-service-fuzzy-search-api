package uk.nhs.digital.uec.api.authentication.exception;

public class InvalidCredentialsException extends RuntimeException {

  public InvalidCredentialsException(String message) {
    super(message);
  }
}

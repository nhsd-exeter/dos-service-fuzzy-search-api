package uk.nhs.digital.uec.api.exception;

public class AccessTokenExpiredException extends Exception {
  public AccessTokenExpiredException(String message) {
    super(message);
  }
}

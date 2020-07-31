package uk.nhs.digital.uec.api.exception;

import lombok.Getter;

@Getter
public class ValidationException extends Exception {

  private static final long serialVersionUID = -5305538934171625716L;
  private String validationCode;

  public ValidationException(String message, String validationCode) {
    super(message);
    this.validationCode = validationCode;
  }
}

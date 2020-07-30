package uk.nhs.digital.uec.api.exception;

import lombok.Getter;

@Getter
public class ValidationException extends Exception {

  private static final long serialVersionUID = -5305538934171625716L;
  private final String validationCode = "VAL-001";

  public ValidationException(String message) {
    super(message);
  }
}

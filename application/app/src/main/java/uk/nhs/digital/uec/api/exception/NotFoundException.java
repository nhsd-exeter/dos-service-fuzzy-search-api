package uk.nhs.digital.uec.api.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends Exception {

  private static final long serialVersionUID = -5305538934171625716L;

  public NotFoundException(String message) {
    super(message);
  }
}

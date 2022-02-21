package uk.nhs.digital.uec.api.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidParameterException extends Exception {

  public InvalidParameterException(String message) {
    super(message);
  }
}

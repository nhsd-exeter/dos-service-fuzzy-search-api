package uk.nhs.digital.uec.api.exception;

import lombok.Getter;

@Getter
public class GoogleApiException extends RuntimeException {

  public GoogleApiException(String message) {
    super(message);
  }

  public GoogleApiException(String message, Throwable cause) {
    super(message, cause);
  }
}


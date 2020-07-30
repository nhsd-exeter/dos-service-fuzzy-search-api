package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/** Defines the response that is returned from the API when validation errors are encountered. */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"validation_code", "validation_error"})
public class ApiValidationErrorResponse implements ApiResponse {

  @JsonProperty("validation_code")
  private String validationCode;

  @JsonProperty("validation_error")
  private String validationError;

  public ApiValidationErrorResponse(final String validationCode, final String validationError) {
    this.validationCode = validationCode;
    this.validationError = validationError;
  }
}

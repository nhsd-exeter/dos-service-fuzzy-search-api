package uk.nhs.digital.uec.api.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.nhs.digital.uec.api.exception.ErrorMappingEnum.ValidationCodes;
import uk.nhs.digital.uec.api.model.ErrorResponse;

/** Controller advice class for postcode mapping details */
@ControllerAdvice
@Slf4j
public class FuzzySearchControllerAdvice {

  @Autowired private ObjectMapper objectMapper;

  @Value("${configuration.validation.max_search_criteria}")
  private int maxSearchCriteria;

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPostCodeException(Exception exception) {
    log.error(ExceptionUtils.getStackTrace(exception));
    ErrorResponse errorResponse = new ErrorResponse();
    String exceptionMessage = exception.getMessage();

    Optional<ValidationCodes> validationCodesOptional =
        ErrorMappingEnum.getValidationEnum().entrySet().stream()
            .filter(entry -> exceptionMessage.startsWith(entry.getValue()))
            .map(Map.Entry::getKey)
            .findFirst();

    errorResponse.setValidationCode(
        validationCodesOptional.isPresent()
            ? validationCodesOptional.get().getValidationCode()
            : null);

    if (errorResponse.getValidationCode() == null
        && exceptionMessage.contains(String.valueOf(maxSearchCriteria))) {
      errorResponse.setValidationCode(ValidationCodes.VAL002.getValidationCode());
    }
    errorResponse.setMessage(exceptionMessage);
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(HttpStatus.NOT_FOUND.value()));
  }

  @ExceptionHandler(InvalidParameterException.class)
  public ResponseEntity<ErrorResponse> handleException(InvalidParameterException exception)
      throws JsonProcessingException {
    log.error(ExceptionUtils.getStackTrace(exception));
    ErrorResponse errorResponse;
    try {
      errorResponse = objectMapper.readValue(exception.getMessage(), ErrorResponse.class);
      errorResponse.setValidationCode("PMA-" + errorResponse.getValidationCode());
    } catch (JsonProcessingException e) {
      log.error("Error while parsing postcode mapping api error response");
      throw e;
    }
    return new ResponseEntity<>(
        errorResponse, HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> handleException(Exception exception) {
    log.error(ExceptionUtils.getStackTrace(exception));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()));
  }
}

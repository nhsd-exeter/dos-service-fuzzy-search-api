package uk.nhs.digital.uec.api.exception;

import java.util.EnumMap;

/** This class maps the validation error code with the relevant message */
public class ErrorMappingEnum {

  public enum ValidationCodes {
    VAL001("VAL-001"),
    VAL002("VAL-002"),
    VAL003("VAL-003"),
    VAL004("VAL-004");

    private String validationCode;

    private ValidationCodes(String s) {
      validationCode = s;
    }

    public String getValidationCode() {
      return validationCode;
    }
  }

  public static EnumMap<ValidationCodes, String> getValidationEnum() {
    EnumMap<ValidationCodes, String> codesMapping = new EnumMap<>(ValidationCodes.class);
    codesMapping.put(ValidationCodes.VAL001, ErrorMessageEnum.NO_SEARCH_CRITERIA.getMessage());
    codesMapping.put(ValidationCodes.VAL002, ErrorMessageEnum.MAXIMUM_PARAMS_EXCEEDED.getMessage());
    codesMapping.put(
        ValidationCodes.VAL003, ErrorMessageEnum.MINIMUM_PARAMS_NOT_PASSED.getMessage());
    codesMapping.put(ValidationCodes.VAL004, ErrorMessageEnum.INVALID_LOCATION.getMessage());
    return codesMapping;
  }
}

package uk.nhs.digital.uec.api.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/** This class maps the validation error code with the relevant message */
public class ErrorMappingEnum {

  public enum ValidationCodes {
    VAL001("VAL-001"),
    VAL002("VAL-002"),
    VAL003("VAL-003"),
    VAL004("VAL-004"),
    VAL005("VAL-005");

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
    codesMapping.put(ValidationCodes.VAL005, ErrorMessageEnum.INVALID_LAT_LON_VALUES_OR_INVALID_POSTCODE.getMessage());
    return codesMapping;
  }

  public static <K, V> Stream<K> getKeys(Map<K, V> map, V value) {
    return map
      .entrySet()
      .stream()
      .filter(entry -> value.equals(entry.getValue()))
      .map(Map.Entry::getKey);
  }

}

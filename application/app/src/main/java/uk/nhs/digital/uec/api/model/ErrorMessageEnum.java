package uk.nhs.digital.uec.api.model;

/** This class defines the messages that will be used while throwing relevant exceptions */
public enum ErrorMessageEnum {
  INVALID_POSTCODE("Postcode is provided but it is invalid"),
  NO_LOCATION_FOUND("No location details found for the given name or postcode"),
  NO_PARAMS_PROVIDED("No postcode or name provided"),
  NO_SEARCH_CRITERIA(
      "No search criteria has been given. Please pass through at least one search term."),
  MAXIMUM_PARAMS_EXCEEDED(
      "The number of search terms entered exceeds the maximum number of terms that can be"
          + " applied. The maximum number of terms that can be applied is {0}"),
  MINIMUM_PARAMS_NOT_PASSED(
      "None of the search criteria given meets the minimum required search criteria length."),
  INVALID_LOCATION("Invalid search location"),
  INVALID_LAT_LON_VALUES("Valid location (lat and lon) values or postcode is  required");

  private String message;

  private ErrorMessageEnum(String s) {
    message = s;
  }

  public String getMessage() {
    return message;
  }
}

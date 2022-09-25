package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines the successful response that is returned from the API when a successful search is
 * performed.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "search_criteria",
  "fuzz_level",
  "address_priority",
  "name_priority",
  "postcode_priority",
  "public_name_priority",
  "max_number_of_services_to_return"
})
public class ApiSearchParamsResponse {

  @JsonProperty("search_criteria")
  private List<String> searchCriteria;

  @JsonProperty("search_location")
  private String searchPostcode;

  @JsonProperty("search_latitude")
  private String searchLatitude;

  @JsonProperty("search_longitude")
  private String searchLongitude;

  @JsonProperty("distance_range")
  private String distanceRange;

  @JsonProperty("referral_role")
  private String referralRole;


  @JsonProperty("fuzz_level")
  private Object fuzzLevel;

  @JsonProperty("address_priority")
  private int addressPriority;

  @JsonProperty("name_priority")
  private int namePriority;

  @JsonProperty("postcode_priority")
  private int postcodePriority;

  @JsonProperty("public_name_priority")
  private int publicNamePriority;

  @JsonProperty("max_number_of_services_to_return")
  private int maxNumServicesToReturn;

  public ApiSearchParamsResponse() {}

  private ApiSearchParamsResponse(ApiSearchParamsResponseBuilder builder) {
    this.searchCriteria = builder.searchCriteria;
    this.searchPostcode = builder.searchPostcode;
    this.searchLatitude = builder.searchLatitude;
    this.distanceRange = builder.distanceRange;
    this.referralRole = builder.referralRole;
    this.searchLongitude = builder.searchLongitude;
    this.fuzzLevel = builder.fuzzLevel;
    this.addressPriority = builder.addressPriority;
    this.namePriority = builder.namePriority;
    this.postcodePriority = builder.postcodePriority;
    this.publicNamePriority = builder.publicNamePriority;
    this.maxNumServicesToReturn = builder.maxNumServicesToReturn;
  }

  public static class ApiSearchParamsResponseBuilder {
    private List<String> searchCriteria;
    private String searchPostcode;
    private String searchLatitude;
    private String searchLongitude;
    private String distanceRange;
    private String referralRole;
    private Object fuzzLevel;
    private int addressPriority;
    private int postcodePriority;
    private int publicNamePriority;
    private int namePriority;
    private int maxNumServicesToReturn;

    public ApiSearchParamsResponseBuilder searchCriteria(List<String> searchCriteria) {
      this.searchCriteria = searchCriteria;
      return this;
    }

    public ApiSearchParamsResponseBuilder searchPostcode(String searchPostcode) {
      this.searchPostcode = searchPostcode;
      return this;
    }

    public ApiSearchParamsResponseBuilder searchLatitude(String searchLatitude) {
      this.searchLatitude = searchLatitude;
      return this;
    }

    public ApiSearchParamsResponseBuilder searchLongitude(String searchLongitude) {
      this.searchLongitude = searchLongitude;
      return this;
    }


    public ApiSearchParamsResponseBuilder distanceRange(String distanceRange) {
      this.distanceRange = distanceRange;
      return this;
    }

    public ApiSearchParamsResponseBuilder referralRole(String referralRole) {
      this.referralRole = referralRole;
      return this;
    }


    public ApiSearchParamsResponseBuilder fuzzLevel(Object fuzzLevel) {
      this.fuzzLevel = fuzzLevel;
      return this;
    }

    public ApiSearchParamsResponseBuilder addressPriority(Integer addressPriority) {
      this.addressPriority = addressPriority;
      return this;
    }

    public ApiSearchParamsResponseBuilder postcodePriority(Integer postcodePriority) {
      this.postcodePriority = postcodePriority;
      return this;
    }

    public ApiSearchParamsResponseBuilder publicNamePriority(Integer publicNamePriority) {
      this.publicNamePriority = publicNamePriority;
      return this;
    }

    public ApiSearchParamsResponseBuilder namePriority(Integer namePriority) {
      this.namePriority = namePriority;
      return this;
    }

    public ApiSearchParamsResponseBuilder maxNumServicesToReturn(Integer maxNumServicesToReturn) {
      this.maxNumServicesToReturn = maxNumServicesToReturn;
      return this;
    }

    public ApiSearchParamsResponse build() {
      return new ApiSearchParamsResponse(this);
    }
  }
}

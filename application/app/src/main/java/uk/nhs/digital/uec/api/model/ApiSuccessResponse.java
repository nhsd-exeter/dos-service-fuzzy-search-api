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
  "max_num_services_to_return",
  "number_of_services",
  "services"
})
public class ApiSuccessResponse implements ApiResponse {

  @JsonProperty("search_criteria")
  private List<String> searchCriteria;

  @JsonProperty("fuzz_level")
  private int fuzzLevel;

  @JsonProperty("max_num_services_to_return")
  private int maxNumServicesToReturn;

  @JsonProperty("number_of_services")
  private int numberOfServices;

  @JsonProperty("services")
  private List<DosService> services;

  public ApiSuccessResponse() {}

  private ApiSuccessResponse(ApiSuccessResponseBuilder builder) {
    this.searchCriteria = builder.searchCriteria;
    this.fuzzLevel = builder.fuzzLevel;
    this.maxNumServicesToReturn = builder.maxNumServicesToReturn;
  }

  public void setServices(final List<DosService> dosServices) {
    this.services = dosServices;
    this.numberOfServices = dosServices.size();
  }

  public static class ApiSuccessResponseBuilder {
    private List<String> searchCriteria;
    private int fuzzLevel;
    private int maxNumServicesToReturn;

    public ApiSuccessResponseBuilder searchCriteria(List<String> searchCriteria) {
      this.searchCriteria = searchCriteria;
      return this;
    }

    public ApiSuccessResponseBuilder fuzzLevel(Integer fuzzLevel) {
      if (fuzzLevel == null) {
        fuzzLevel = 0;
      }
      this.fuzzLevel = fuzzLevel;
      return this;
    }

    public ApiSuccessResponseBuilder maxNumServicesToReturn(Integer maxNumServicesToReturn) {
      if (maxNumServicesToReturn == null) {
        maxNumServicesToReturn = 0;
      }
      this.maxNumServicesToReturn = maxNumServicesToReturn;
      return this;
    }

    public ApiSuccessResponse build() {
      return new ApiSuccessResponse(this);
    }
  }
}

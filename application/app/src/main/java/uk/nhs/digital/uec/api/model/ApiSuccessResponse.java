package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * Defines the successful response that is returned from the API when a successful search is
 * performed.
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
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
  "max_num_services_to_return",
  "number_of_services",
  "services"
})
public class ApiSuccessResponse implements ApiResponse {

  @JsonProperty("search_criteria")
  private List<String> searchCriteria;

  @JsonProperty("fuzz_level")
  private int fuzzLevel;

  @JsonProperty("address_priority")
  private int addressPriority;

  @JsonProperty("name_priority")
  private int namePriority;

  @JsonProperty("postcode_priority")
  private int postcodePriority;

  @JsonProperty("public_name_priority")
  private int publicNamePriority;

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
    this.addressPriority = builder.addressPriority;
    this.namePriority = builder.namePriority;
    this.postcodePriority = 0;
    this.publicNamePriority = 0;
    this.maxNumServicesToReturn = builder.maxNumServicesToReturn;
  }

  public void setServices(final List<DosService> dosServices) {
    this.services = dosServices;
    this.numberOfServices = dosServices.size();
  }

  public static class ApiSuccessResponseBuilder {
    private List<String> searchCriteria;
    private int fuzzLevel;
    private int addressPriority;
    private int namePriority;
    private int maxNumServicesToReturn;

    public ApiSuccessResponseBuilder searchCriteria(List<String> searchCriteria) {
      this.searchCriteria = searchCriteria;
      return this;
    }

    public ApiSuccessResponseBuilder fuzzLevel(Integer fuzzLevel) {
      this.fuzzLevel = fuzzLevel;
      return this;
    }

    public ApiSuccessResponseBuilder addressPriority(Integer addressPriority) {
      this.addressPriority = addressPriority;
      return this;
    }

    public ApiSuccessResponseBuilder namePriority(Integer namePriority) {
      this.namePriority = namePriority;
      return this;
    }

    public ApiSuccessResponseBuilder maxNumServicesToReturn(Integer maxNumServicesToReturn) {
      this.maxNumServicesToReturn = maxNumServicesToReturn;
      return this;
    }

    public ApiSuccessResponse build() {
      return new ApiSuccessResponse(this);
    }
  }
}

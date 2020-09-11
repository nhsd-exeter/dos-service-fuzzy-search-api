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
@JsonPropertyOrder({"search_criteria", "number_of_services", "services"})
public class ApiSuccessResponse implements ApiResponse {

  @JsonProperty("search_criteria")
  private List<String> searchCriteria;

  @JsonProperty("number_of_services")
  private int numberOfServices;

  @JsonProperty("services")
  private List<DosService> services;

  public void setServices(final List<DosService> dosServices) {
    this.services = dosServices;
    this.numberOfServices = dosServices.size();
  }
}

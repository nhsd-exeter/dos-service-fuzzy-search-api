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
@JsonPropertyOrder({"number_of_services_found", "services"})
public class ApiSearchResultsResponse {

  @JsonProperty("number_of_services_found")
  private int numberOfServicesFound;

  @JsonProperty("services")
  private List<DosService> services;

  public ApiSearchResultsResponse() {}

  public void setServices(final List<DosService> dosServices) {
    this.services = dosServices;
    this.numberOfServicesFound = dosServices.size();
  }
}

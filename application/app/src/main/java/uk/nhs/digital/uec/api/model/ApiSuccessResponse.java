package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "search_parameters",
  "search_results",
})
public class ApiSuccessResponse implements ApiResponse {

  @JsonProperty("search_parameters")
  private ApiSearchParamsResponse searchParameters;

  @JsonProperty("search_results")
  private ApiSearchResultsResponse searchResults;

  public ApiSuccessResponse() {}
}

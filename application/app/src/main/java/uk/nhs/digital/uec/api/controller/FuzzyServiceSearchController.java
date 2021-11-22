package uk.nhs.digital.uec.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.exception.ValidationException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.ApiResponse;
import uk.nhs.digital.uec.api.model.ApiSearchParamsResponse;
import uk.nhs.digital.uec.api.model.ApiSearchResultsResponse;
import uk.nhs.digital.uec.api.model.ApiSuccessResponse;
import uk.nhs.digital.uec.api.model.ApiValidationErrorResponse;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.service.ValidationServiceInterface;

/** Controller for Fuzzy searching of services. */
@RestController
@RequestMapping("/dosapi/dosservices/v0.0.1")
public class FuzzyServiceSearchController {

  @Autowired private FuzzyServiceSearchServiceInterface fuzzyServiceSearchService;

  @Autowired private ValidationServiceInterface validationService;

  @Autowired private ApiUtilsServiceInterface utils;

  @Autowired private ApiRequestParams requestParams;

  @Value("${configuration.search_parameters.max_num_services_to_return}")
  private String defaultMaxNumServicesToReturn;

  @Value("${configuration.search_parameters.fuzz_level}")
  private String defaultFuzzLevel;

  @Value("${configuration.version}")
  private String apiVersion;

  /** Welcome/home page endpoint for the DoS Service Fuzzy Search API. */
  @RequestMapping(value = "/home")
  public String home() {
    return "This is the DoS Service Fuzzy Search API. Version: " + apiVersion;
  }

  /**
   * Endpoint for retrieving services with attributes that match the search criteria provided.
   *
   * @param searchCriteria the search criteria containing the list of search criteria terms.
   * @param filterReferralRole if passed through, results will be filtered by the referral role
   *     provided.
   * @return {@link ApiResponse}
   */
  @GetMapping("services/byfuzzysearch")
  @CrossOrigin(origins = "*")
  @PreAuthorize("hasAnyRole('FUZZY_API_ACCESS')")
  public ResponseEntity<ApiResponse> getServicesByFuzzySearch(
      @RequestParam(name = "search_term", required = false) List<String> searchCriteria,
      @RequestParam(name = "search_location", required = false) String searchPostcode,
      @RequestParam(name = "filter_referral_role", required = false) String filterReferralRole,
      @RequestParam(name = "max_num_services_to_return_from_es", required = false)
          Integer maxNumServicesToReturnFromEs,
      @RequestParam(name = "max_number_of_services_to_return", required = false)
          Integer maxNumServicesToReturn,
      @RequestParam(name = "fuzz_level", required = false) Integer fuzzLevel,
      @RequestParam(name = "name_priority", required = false) Integer namePriority,
      @RequestParam(name = "address_priority", required = false) Integer addressPriority,
      @RequestParam(name = "postcode_priority", required = false) Integer postcodePriority,
      @RequestParam(name = "public_name_priority", required = false) Integer publicNamePriority,
      @RequestHeader MultiValueMap<String, String> headers) {

    utils.configureApiRequestParams(
        fuzzLevel,
        filterReferralRole,
        maxNumServicesToReturnFromEs,
        maxNumServicesToReturn,
        namePriority,
        addressPriority,
        postcodePriority,
        publicNamePriority);

    final ApiSearchParamsResponse searchParamsResponse =
        new ApiSearchParamsResponse.ApiSearchParamsResponseBuilder()
            .searchCriteria(searchCriteria)
            .searchLocation(searchPostcode)
            .fuzzLevel(requestParams.getFuzzLevel())
            .addressPriority(requestParams.getAddressPriority())
            .postcodePriority(requestParams.getPostcodePriority())
            .publicNamePriority(requestParams.getPublicNamePriority())
            .namePriority(requestParams.getNamePriority())
            .maxNumServicesToReturn(requestParams.getMaxNumServicesToReturn())
            .build();

    final ApiSuccessResponse response = new ApiSuccessResponse();
    final ApiSearchResultsResponse searchResultsResponse = new ApiSearchResultsResponse();

    try {
      validationService.validateSearchCriteria(searchCriteria);
      validationService.validateSearchLocation(searchPostcode);
      validationService.validateMinSearchCriteriaLength(searchCriteria);

      final List<DosService> dosServices =
          fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchPostcode, searchCriteria);
      searchResultsResponse.setServices(dosServices);
      response.setSearchParameters(searchParamsResponse);
      response.setSearchResults(searchResultsResponse);
    } catch (final ValidationException ex) {
      final ApiValidationErrorResponse valResponse =
          new ApiValidationErrorResponse(ex.getValidationCode(), ex.getMessage());
      return ResponseEntity.badRequest().body(valResponse);
    }

    return ResponseEntity.ok(response);
  }
}

package uk.nhs.digital.uec.api.controller;

import static uk.nhs.digital.uec.api.authentication.constants.SwaggerConstants.ADDRESS_PRIORITY_DESC;
import static uk.nhs.digital.uec.api.authentication.constants.SwaggerConstants.FUZZ_LEVEL_DESC;
import static uk.nhs.digital.uec.api.authentication.constants.SwaggerConstants.MAX_NUM_SERVICES_DESC;
import static uk.nhs.digital.uec.api.authentication.constants.SwaggerConstants.NAME_PRIORITY_DESC;
import static uk.nhs.digital.uec.api.authentication.constants.SwaggerConstants.POSTCODE_PRIORITY_DESC;
import static uk.nhs.digital.uec.api.authentication.constants.SwaggerConstants.PUBLIC_NAME_PRIORITY_DESC;
import static uk.nhs.digital.uec.api.authentication.constants.SwaggerConstants.SEARCH_CRITERIA_DESC;
import static uk.nhs.digital.uec.api.authentication.constants.SwaggerConstants.SEARCH_POSTCODE_DESC;

import io.swagger.annotations.ApiParam;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.ApiResponse;
import uk.nhs.digital.uec.api.model.ApiSearchParamsResponse;
import uk.nhs.digital.uec.api.model.ApiSearchResultsResponse;
import uk.nhs.digital.uec.api.model.ApiSuccessResponse;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.service.ValidationServiceInterface;

/** Controller for Fuzzy searching of services. */
@RestController
@Slf4j
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

  /**
   * Endpoint for retrieving services with attributes that match the search criteria provided.
   *
   * @param searchCriteria the search criteria containing the list of search criteria terms.
   * @param filterReferralRole if passed through, results will be filtered by the referral role
   *     provided.
   * @return {@link ApiResponse}
   * @throws NotFoundException
   * @throws InvalidParameterException
   */
  @GetMapping("/services/byfuzzysearch")
  @CrossOrigin(origins = "*")
  @PreAuthorize("hasAnyRole('FUZZY_API_ACCESS')")
  public ResponseEntity<ApiResponse> getServicesByFuzzySearch(
      @ApiParam(SEARCH_CRITERIA_DESC) @RequestParam(name = "search_term", required = false)
          List<String> searchCriteria,
      @ApiParam(SEARCH_POSTCODE_DESC) @RequestParam(name = "search_location", required = false)
          String searchPostcode,
      @ApiParam(hidden = true)
          @RequestParam(name = "max_num_services_to_return_from_es", required = false)
          Integer maxNumServicesToReturnFromEs,
      @ApiParam(MAX_NUM_SERVICES_DESC)
          @RequestParam(name = "max_number_of_services_to_return", required = false)
          Integer maxNumServicesToReturn,
      @ApiParam(FUZZ_LEVEL_DESC) @RequestParam(name = "fuzz_level", required = false)
          Integer fuzzLevel,
      @ApiParam(NAME_PRIORITY_DESC) @RequestParam(name = "name_priority", required = false)
          Integer namePriority,
      @ApiParam(ADDRESS_PRIORITY_DESC) @RequestParam(name = "address_priority", required = false)
          Integer addressPriority,
      @ApiParam(POSTCODE_PRIORITY_DESC) @RequestParam(name = "postcode_priority", required = false)
          Integer postcodePriority,
      @ApiParam(PUBLIC_NAME_PRIORITY_DESC)
          @RequestParam(name = "public_name_priority", required = false)
          Integer publicNamePriority)
      throws NotFoundException, InvalidParameterException {
    log.info("Incoming request param - postcode: {}", searchPostcode);
    log.info("Incoming request param - search_term: {}", searchCriteria);
    utils.configureApiRequestParams(
        fuzzLevel,
        null, // filterReferralRole will be implemented in future
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

    final List<DosService> dosServices =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchPostcode, searchCriteria);
    searchResultsResponse.setServices(dosServices);
    response.setSearchParameters(searchParamsResponse);
    response.setSearchResults(searchResultsResponse);

    return ResponseEntity.ok(response);
  }
}

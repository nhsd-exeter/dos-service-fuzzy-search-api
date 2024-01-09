package uk.nhs.digital.uec.api.controller;

import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.*;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.ConcurrentFuzzySearchService;
import uk.nhs.digital.uec.api.util.Constants;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static uk.nhs.digital.uec.api.authentication.constants.SwaggerConstants.*;

@RestController
@Slf4j
@RequestMapping("/dosapi/dosservices/v2")
public class FuzzySearchConcurrentSearchController {
  @Autowired private ConcurrentFuzzySearchService concurrentFuzzySearchService;
  @Autowired private ApiUtilsServiceInterface utils;
  @Autowired private ApiRequestParams requestParams;

  @Value("${configuration.search_parameters.max_num_services_to_return}")
  private String defaultMaxNumServicesToReturn;

  @Value("${configuration.search_parameters.fuzz_level}")
  private String defaultFuzzLevel;

  /**
   * Endpoint for retrieving services with attributes that match the search
   * criteria provided.
   *
   * @param searchCriteria     the search criteria containing the list of search
   *                           criteria terms.
   * @param filterReferralRole if passed through, results will be filtered by the
   *                           referral role
   *                           provided.
   * @return {@link ApiResponse}
   * @throws NotFoundException
   * @throws InvalidParameterException
   */
  @GetMapping("/services/byfuzzysearch")
  @CrossOrigin(origins = "*")
  @PreAuthorize("hasAnyRole('FUZZY_API_ACCESS')")
  public CompletableFuture<ResponseEntity<ApiResponse>> getServicesByFuzzySearch(
    @ApiParam(SEARCH_CRITERIA_DESC) @RequestParam(name = "search_term", required = false) List<String> searchCriteria,
    @ApiParam(SEARCH_POSTCODE_DESC) @RequestParam(name = "search_location") String searchPostcode,
    @ApiParam(SEARCH_LATITUDE_DESC) @RequestParam(name = "search_latitude") String searchLatitude,
    @ApiParam(SEARCH_LONGITUDE_DESC) @RequestParam(name = "search_longitude") String searchLongitude,
    @ApiParam(DISTANCE_RANGE_DESC) @RequestParam(name = "distance_range", required = false) Double distanceRange,
    @ApiParam(REFERRAL_ROLE_DESC) @RequestParam(name = "referral_role", required = false) String referralRole,
    @ApiParam(hidden = true) @RequestParam(name = "max_num_services_to_return_from_es", required = false) Integer maxNumServicesToReturnFromEs,
    @ApiParam(MAX_NUM_SERVICES_DESC) @RequestParam(name = "max_number_of_services_to_return", required = false, defaultValue = "50") Integer maxNumServicesToReturn,
    @ApiParam(FUZZ_LEVEL_DESC) @RequestParam(name = "fuzz_level", required = false) Integer fuzzLevel,
    @ApiParam(NAME_PRIORITY_DESC) @RequestParam(name = "name_priority", required = false) Integer namePriority,
    @ApiParam(ADDRESS_PRIORITY_DESC) @RequestParam(name = "address_priority", required = false) Integer addressPriority,
    @ApiParam(POSTCODE_PRIORITY_DESC) @RequestParam(name = "postcode_priority", required = false) Integer postcodePriority,
    @ApiParam(PUBLIC_NAME_PRIORITY_DESC) @RequestParam(name = "public_name_priority", required = false) Integer publicNamePriority)
    throws NotFoundException, InvalidParameterException, ExecutionException, InterruptedException {

    final ApiSuccessResponse response = new ApiSuccessResponse();
    final ApiSearchResultsResponse searchResultsResponse = new ApiSearchResultsResponse();

    long start = System.currentTimeMillis();
    distanceRange = Objects.isNull(distanceRange) ? Constants.DEFAULT_DISTANCE_RANGE : distanceRange;
    log.info(
      "Incoming request param - postcode: {}, search_term: {}, search_latitude: {},"
        + " search_longitude:{} and using distanceRange: {}",
      searchPostcode,
      searchCriteria,
      searchLatitude,
      searchLongitude, distanceRange);

    utils.configureApiRequestParams(
      fuzzLevel,
      referralRole, // filterReferralRole will be implemented in future
      maxNumServicesToReturnFromEs,
      maxNumServicesToReturn,
      namePriority,
      addressPriority,
      postcodePriority,
      publicNamePriority);

    final ApiSearchParamsResponse searchParamsResponse = buildSearchParamsResponse(searchLatitude,
      searchLongitude,
      distanceRange,
      searchCriteria,
      searchPostcode);

    log.info("Staring concurrent data fetch");
    CompletableFuture<ResponseEntity<ApiResponse>> result = concurrentFuzzySearchService.fuzzySearch(
        searchLatitude, searchLongitude, distanceRange, searchCriteria, searchPostcode,maxNumServicesToReturn)
      .thenApply(dosServicesList -> {
        log.info("Completing async data fetch now combining results");
        searchResultsResponse.setServices(dosServicesList);
        response.setSearchParameters(searchParamsResponse);
        response.setSearchResults(searchResultsResponse);
        return ResponseEntity.ok(response);
      });

    log.info("Elapsed time: {}ms", (System.currentTimeMillis() - start));

    return result;
  }

  private ApiSearchParamsResponse buildSearchParamsResponse(String searchLatitude, String searchLongitude, Double distanceRange, List<String> searchCriteria, String searchPostcode) {
    return new ApiSearchParamsResponse.ApiSearchParamsResponseBuilder()
      .searchCriteria(searchCriteria)
      .searchPostcode(searchPostcode)
      .searchLatitude(searchLatitude)
      .searchLongitude(searchLongitude)
      .distanceRange(distanceRange)
      .referralRole(requestParams.getFilterReferralRole())
      .fuzzLevel(requestParams.getFuzzLevel())
      .addressPriority(requestParams.getAddressPriority())
      .postcodePriority(requestParams.getPostcodePriority())
      .publicNamePriority(requestParams.getPublicNamePriority())
      .namePriority(requestParams.getNamePriority())
      .maxNumServicesToReturn(requestParams.getMaxNumServicesToReturn())
      .build();
  }

}

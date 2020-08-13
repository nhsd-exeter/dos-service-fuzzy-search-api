package uk.nhs.digital.uec.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.exception.ValidationException;
import uk.nhs.digital.uec.api.model.ApiResponse;
import uk.nhs.digital.uec.api.model.ApiSuccResponse;
import uk.nhs.digital.uec.api.model.ApiValidationErrorResponse;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.service.ValidationServiceInterface;

/** Controller for Fuzzy searching of services. */
@RestController
@RequestMapping("/dosapi/dosservices/v0.0.1")
public class FuzzyServiceSearchController {

  @Autowired private FuzzyServiceSearchServiceInterface fuzzyServiceSearchService;

  @Autowired private ValidationServiceInterface validationService;

  /**
   * Endpoint for retrieving services with attributes that match the search criteria provided.
   *
   * @param searchCriteria the search criteria containing the list of search criteria terms.
   * @param filterReferralRole if passed through, results will be filtered by the referral role
   *     provided.
   * @return {@link ApiResponse}
   */
  @RequestMapping("services/byfuzzysearch")
  public ResponseEntity<ApiResponse> getServicesByFuzzySearch(
      @RequestParam(name = "search_criteria", required = false) List<String> searchCriteria,
      @RequestParam(name = "filter_referral_role", required = false) String filterReferralRole) {

    final ApiSuccResponse response = new ApiSuccResponse();
    response.setSearchCriteria(searchCriteria);

    try {
      validationService.validateSearchCriteria(searchCriteria);
      validationService.validateMinSearchCriteriaLength(searchCriteria);

      final List<DosService> dosServices =
          fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchCriteria);
      response.setServices(dosServices);
    } catch (final ValidationException ex) {
      final ApiValidationErrorResponse valResponse =
          new ApiValidationErrorResponse(ex.getValidationCode(), ex.getMessage());
      return ResponseEntity.badRequest().body(valResponse);
    }

    return ResponseEntity.ok(response);
  }
}

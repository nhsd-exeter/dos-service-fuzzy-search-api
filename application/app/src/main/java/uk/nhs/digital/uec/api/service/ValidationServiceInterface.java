package uk.nhs.digital.uec.api.service;

import java.util.List;
import uk.nhs.digital.uec.api.exception.NotFoundException;

/** Interface encapsulating validation processes used by the API. */
public interface ValidationServiceInterface {

  /**
   * Validates that the search criteria provided meets business requirements. Namely that the search
   * criteria must not be empty or null, and that the number of search terms does not exceed a
   * defined value.
   *
   * @param searchCriteria the searchCriteria list to validate.
   * @throws NotFoundException when business requirements are not met.
   */
  void validateSearchCriteria(final List<String> searchCriteria) throws NotFoundException;
  boolean isPostcodeValid(String postcode);
}

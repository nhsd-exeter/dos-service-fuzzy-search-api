package uk.nhs.digital.uec.api.service;

import java.util.List;
import uk.nhs.digital.uec.api.exception.ValidationException;

/** Interface encapsulating validation processes used by the API. */
public interface ValidationServiceInterface {

  /**
   * Validates that the search criteria provided meets business requirements. Namely that the search
   * criteria must not be empty or null, and that the number of search terms does not exceed a
   * defined value.
   *
   * @param searchCriteria the searchCriteria string to validate.
   * @throws ValidationException when business requirements are not met.
   */
  public void validateSearchCriteria(final String searchCriteria) throws ValidationException;

  /**
   * Validates that at least one of the search criteria terms meets the minimum length required.
   *
   * @param searchCriteria the searchCriteria string to validate.
   * @throws ValidationException when none of the search criteria terms meet the minimum length
   *     required.
   */
  public void validateMinSearchCriteriaLength(final List<String> searchCriteria)
      throws ValidationException;
}

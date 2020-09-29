package uk.nhs.digital.uec.api.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.ValidationException;
import uk.nhs.digital.uec.api.service.ValidationServiceInterface;

@Service
public class ValidationService implements ValidationServiceInterface {

  @Value("${param.validation.min_search_term_length}")
  private int minSearchTermLength;

  @Value("${param.validation.max_search_criteria}")
  private int maxSearchCriteria;

  /** {@inheritDoc} */
  @Override
  public void validateSearchCriteria(final List<String> searchCriteria) throws ValidationException {

    if (searchCriteria == null || searchCriteria.isEmpty()) {
      throw new ValidationException(
          "No search criteria has been given. Please pass through at least one search term.",
          "VAL-001");
    }

    if (searchCriteria.size() > maxSearchCriteria) {
      throw new ValidationException(
          "The number of search terms given ("
              + searchCriteria.size()
              + ") given exceeds the maximum number of terms that can be applied. The maximum"
              + " number of terms that can be applied is "
              + maxSearchCriteria,
          "VAL-002");
    }
  }

  /** {@inheritDoc} */
  @Override
  public void validateMinSearchCriteriaLength(final List<String> searchCriteria)
      throws ValidationException {

    if (searchCriteria == null || searchCriteria.isEmpty()) {
      throw new ValidationException(
          "No search criteria has been given. Please pass through at least one search term.",
          "VAL-001");
    }

    // Check that at least one of the search criteria provided meets the min search criteria length
    // requirement.

    boolean minSearchCriteriaLthMet = false;

    for (final String searchCriteriaStr : searchCriteria) {
      if (searchCriteriaStr.length() >= minSearchTermLength) {
        minSearchCriteriaLthMet = true;
        break;
      }
    }

    if (!minSearchCriteriaLthMet) {
      throw new ValidationException(
          "None of the search criteria given meets the minimum required search criteria length.",
          "VAL-003");
    }
  }
}

package uk.nhs.digital.uec.api.service.impl;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.ValidationException;
import uk.nhs.digital.uec.api.service.ValidationServiceInterface;

@Service
public class ValidationService implements ValidationServiceInterface {

  @Value("${param.validation.min_search_string_length}")
  private int minSearchStringLength;

  @Value("${param.validation.max_search_criteria}")
  private int maxSearchCriteria;

  @Value("${param.business.search_criteria_delimiter}")
  private String searchCriteriaDelimiter;

  public void validateSearchCriteria(final String searchCriteria) throws ValidationException {

    if (searchCriteria == null || searchCriteria.isEmpty()) {
      throw new ValidationException("No search criteria, or blank search criteria given.");
    }

    int numberOfCriteria = (StringUtils.countMatches(searchCriteria, searchCriteriaDelimiter) + 1);
    if (numberOfCriteria > maxSearchCriteria) {
      throw new ValidationException("Too many search criteria (" + numberOfCriteria + ") given.");
    }
  }

  public void validateMinSearchCriteriaLength(final List<String> searchCriteria)
      throws ValidationException {

    // Check that at least one of the search criteria provided meets the min search criteria length
    // requirement.

    boolean minSearchCriteriaLthMet = false;

    for (final String searchCriteriaStr : searchCriteria) {
      if (searchCriteriaStr.length() > minSearchStringLength) {
        minSearchCriteriaLthMet = true;
        break;
      }
    }

    if (!minSearchCriteriaLthMet) {
      throw new ValidationException(
          "None of the search criteria given meets the minimum required search criteria length.");
    }
  }
}

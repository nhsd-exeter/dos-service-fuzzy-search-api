package uk.nhs.digital.uec.api.service.impl;

import java.text.MessageFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.ErrorMessageEnum;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.service.ValidationServiceInterface;

@Service
public class ValidationService implements ValidationServiceInterface {

  @Value("${configuration.validation.min_search_term_length}")
  private int minSearchTermLength;

  @Value("${configuration.validation.max_search_criteria}")
  private int maxSearchCriteria;

  /** {@inheritDoc} */
  @Override
  public void validateSearchCriteria(final List<String> searchCriteria) throws NotFoundException {

    boolean minSearchCriteriaLengthMet = false;

    if (searchCriteria == null || searchCriteria.isEmpty()) {
      throw new NotFoundException(ErrorMessageEnum.NO_SEARCH_CRITERIA.getMessage());
    }
    if (searchCriteria.size() > maxSearchCriteria) {
      throw new NotFoundException(
          MessageFormat.format(
              ErrorMessageEnum.MAXIMUM_PARAMS_EXCEEDED.getMessage(), maxSearchCriteria));
    }
    for (final String searchCriteriaStr : searchCriteria) {
      if (searchCriteriaStr.length() >= minSearchTermLength) {
        minSearchCriteriaLengthMet = true;
        break;
      }
    }
    if (!minSearchCriteriaLengthMet) {
      throw new NotFoundException(ErrorMessageEnum.MINIMUM_PARAMS_NOT_PASSED.getMessage());
    }
  }
}

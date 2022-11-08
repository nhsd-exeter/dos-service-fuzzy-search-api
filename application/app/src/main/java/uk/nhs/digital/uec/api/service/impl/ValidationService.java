package uk.nhs.digital.uec.api.service.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.ErrorMessageEnum;
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

    boolean minSearchCriteriaLthMet = false;

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
        minSearchCriteriaLthMet = true;
        break;
      }
    }
    if (!minSearchCriteriaLthMet) {
      throw new NotFoundException(ErrorMessageEnum.MINIMUM_PARAMS_NOT_PASSED.getMessage());
    }
  }

  @Override
  public boolean isPostcodeValid(String postcode) {
    final String POSTCODE_REGEX =
      "([Gg][Ii][Rr]"
        + " 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";
    Pattern pattern = Pattern.compile(POSTCODE_REGEX);
    return pattern.matcher(postcode).matches();
  }
}

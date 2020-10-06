package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;

@Service
public class ApiUtilsService implements ApiUtilsServiceInterface {

  @Autowired private ApiRequestParams apiRequestParams;

  @Value("${param.validation.min_search_term_length}")
  private int minSearchTermLength;

  public void configureApiRequestParams(
      Integer fuzzLevel,
      String referralRole,
      Integer maxNumServicesToReturn,
      Integer namePriority,
      Integer addressPriority) {
    apiRequestParams.setFuzzLevel(fuzzLevel);
    apiRequestParams.setFilterReferralRole(referralRole);
    apiRequestParams.setMaxNumServicesToReturn(maxNumServicesToReturn);
    apiRequestParams.setNamePriority(namePriority);
    apiRequestParams.setAddressPriority(addressPriority);
  }

  /** {@inheritDoc} */
  @Override
  public List<String> sanitiseSearchTerms(final List<String> searchCriteria) {

    List<String> listFromString = new ArrayList<>();
    listFromString = searchCriteria.stream().map(String::trim).collect(Collectors.toList());

    // Now remove terms that are less than the min amount required.
    List<String> sanitisedSearchTerms = new ArrayList<>();
    sanitisedSearchTerms.addAll(listFromString);
    for (String searchTerm : listFromString) {
      if (searchTerm.length() < minSearchTermLength) {
        sanitisedSearchTerms.remove(searchTerm);
      }
    }

    return sanitisedSearchTerms;
  }
}

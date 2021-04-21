package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
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

  @Value("${configuration.validation.min_search_term_length}")
  private int minSearchTermLength;

  public void configureApiRequestParams(
      Integer fuzzLevel,
      String referralRole,
      Integer maxNumServicesToReturnFromEs,
      Integer maxNumServicesToReturn,
      Integer namePriority,
      Integer addressPriority,
      Integer postcodePriority,
      Integer publicNamePriority) {
    apiRequestParams.setFuzzLevel(fuzzLevel);
    apiRequestParams.setFilterReferralRole(referralRole);
    apiRequestParams.setMaxNumServicesToReturnFromElasticsearch(maxNumServicesToReturnFromEs);
    apiRequestParams.setMaxNumServicesToReturn(maxNumServicesToReturn);
    apiRequestParams.setNamePriority(namePriority);
    apiRequestParams.setAddressPriority(addressPriority);
    apiRequestParams.setPostcodePriority(postcodePriority);
    apiRequestParams.setPublicNamePriority(publicNamePriority);
  }

  /** {@inheritDoc} */
  @Override
  public List<String> sanitiseSearchTerms(final List<String> searchCriteria) {

    List<String> listFromString = new ArrayList<>();
    listFromString = searchCriteria.stream().map(String::trim).collect(Collectors.toList());

    List<String> sanitisedSearchTerms = new ArrayList<>();

    for (String searchTerm : listFromString) {
      if (searchTerm.length() >= minSearchTermLength) {

        String searchTermToAdd = searchTerm.replaceAll("\\s+", " ");

        // Remove weird characters
        searchTermToAdd.replaceAll("[^a-zA-Z0-9:;.?! ]", "");

        sanitisedSearchTerms.add(searchTermToAdd);

        addAdditionalSearchTerms(sanitisedSearchTerms, searchTerm);
      }
    }

    return sanitisedSearchTerms;
  }

  /** {@inheritDoc} */
  @Override
  public String removeBlankSpaces(final String field) {
    if (field != null) {
      return field.replaceAll("\\s", "");
    }

    return "";
  }

  @Override
  public List<String> removeBlankSpacesIn(final List<String> fields) {
    List<String> collect =
        fields.stream().map(this::removeBlankSpaces).collect(Collectors.toList());
    return collect;
  }

  /**
   * Adds additional search terms to the list of search criteria originally passed through for
   * enhanced searching capabilities
   *
   * @param sanitisedSearchTerms the current list of search terms to add the additional search terms
   *     to.
   * @param searchTerm the current search term used to derive the additional search terms from.
   */
  private void addAdditionalSearchTerms(List<String> sanitisedSearchTerms, String searchTerm) {
    List<String> subSearchTerms = Arrays.asList(searchTerm.split("\\s+"));
    String previousSubSearchTerm = null;
    for (String subSearchTerm : subSearchTerms) {

      if (previousSubSearchTerm != null) {
        sanitisedSearchTerms.add(previousSubSearchTerm + subSearchTerm);
      }

      previousSubSearchTerm = subSearchTerm;
    }
  }
}

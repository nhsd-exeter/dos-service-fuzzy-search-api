package uk.nhs.digital.uec.api.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;

@Service
@Slf4j
public class ApiUtilsService implements ApiUtilsServiceInterface {

  @Value("${configuration.validation.min_search_term_length}")
  private int minSearchTermLength;

  private final ApiRequestParams apiRequestParams;

  @Autowired
  public ApiUtilsService(ApiRequestParams apiRequestParams) {
    this.apiRequestParams = apiRequestParams;
  }

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
    log.info("Sanitising search terms {}", searchCriteria);
    List<String> listFromString =
        searchCriteria.stream().map(String::trim).collect(Collectors.toList());

    List<String> sanitisedSearchTerms = new ArrayList<>();

    for (String searchTerm : listFromString) {
      if (searchTerm.length() >= minSearchTermLength) {
        String searchTermToAdd = searchTerm.replaceAll("\\s+", " ");

        // Remove URL encoding
        try {
          searchTermToAdd = URLDecoder.decode(searchTermToAdd, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          log.error(e.getMessage());
        }

        // Remove weird characters
        searchTermToAdd = searchTermToAdd.replaceAll("[^a-zA-Z0-9:;.?! ]", "");

        sanitisedSearchTerms.add(searchTermToAdd);

        addAdditionalSearchTerms(sanitisedSearchTerms, searchTerm);
      }
    }
    log.info("Sanitised search terms {}", sanitisedSearchTerms);
    return sanitisedSearchTerms;
  }

  /** {@inheritDoc} */
  @Override
  public String removeBlankSpaces(final String field) {
    log.info("Formatting {}", field);
    if (field != null) {
      return field.replaceAll("\\s", "");
    }
    return "";
  }

  @Override
  public List<String> removeBlankSpacesIn(final List<String> fields) {
    final String POSTCODE_REGEX =
        "^[A-Za-z][A-HJ-Ya-hj-y]?\\d[A-Za-z0-9]? ?\\d[A-Za-z]{2}|[Gg][Ii][Rr] ?0[Aa]{2}$";
    Pattern pattern = Pattern.compile(POSTCODE_REGEX);
    return fields.stream()
        .filter(field -> pattern.matcher(field).matches())
        .map(this::removeBlankSpaces)
        .collect(Collectors.toList());
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

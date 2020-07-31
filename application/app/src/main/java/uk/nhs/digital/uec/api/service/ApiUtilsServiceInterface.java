package uk.nhs.digital.uec.api.service;

import java.util.List;

/** Interface encapsulating useful utilities that can be used throughout the API. */
public interface ApiUtilsServiceInterface {

  /**
   * Takes the list of search criteria and sanitises each search term by removing trailing and
   * leading spaces.
   *
   * @param searchCriteria the search criteria to sanitise.
   * @return List of Strings
   */
  public List<String> sanitiseSearchTerms(final List<String> searchCriteria);
}

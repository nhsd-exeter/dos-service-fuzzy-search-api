package uk.nhs.digital.uec.api.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/** Interface encapsulating useful utilities that can be used throughout the API. */
public interface ApiUtilsServiceInterface {

  /**
   * Configures the internal api request parameters object with the parameters passed in from the
   * request.
   *
   * @param request {@link HttpServletRequest} of the request.
   */
  void configureApiRequestParams(HttpServletRequest request);

  /**
   * Takes the list of search criteria and sanitises each search term by removing trailing and
   * leading spaces.
   *
   * @param searchCriteria the search criteria to sanitise.
   * @return List of Strings
   */
  List<String> sanitiseSearchTerms(final List<String> searchCriteria);
}

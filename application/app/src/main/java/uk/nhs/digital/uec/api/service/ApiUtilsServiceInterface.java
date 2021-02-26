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
  void configureApiRequestParams(
      Integer fuzzLevel,
      String referralRole,
      Integer maxNumServicesToReturnFromEs,
      Integer maxNumServicesToReturn,
      Integer namePriority,
      Integer addressPriority,
      Integer postcodePriority,
      Integer publicNamePriority);

  /**
   * Takes the list of search criteria and sanitises each search term by removing trailing and
   * leading spaces.
   *
   * @param searchCriteria the search criteria to sanitise.
   * @return List of Strings
   */
  List<String> sanitiseSearchTerms(final List<String> searchCriteria);

  /**
   * Removes any spaces from the postcode
   *
   * @param field field to remove spaces from
   * @return field with no spaces
   */
  String removeBlankSpaces(final String field);
}

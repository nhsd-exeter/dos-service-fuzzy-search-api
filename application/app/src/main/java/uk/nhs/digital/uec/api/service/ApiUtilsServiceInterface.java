package uk.nhs.digital.uec.api.service;

import java.util.List;

/** Interface encapsulating useful utilities that can be used throughout the API. */
public interface ApiUtilsServiceInterface {

  /**
   * Takes a String, splits the String by the defined delimiter adding each split into a List of
   * Strings which is returned.
   *
   * @param sourceString the String to split and turn into a List of Strings.
   * @param delimiter the delimiter to split the string.
   * @return List of Strings
   */
  public List<String> createListFromString(final String sourceString, final String delimiter);
}

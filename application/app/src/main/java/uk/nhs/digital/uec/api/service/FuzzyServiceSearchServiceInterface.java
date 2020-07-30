package uk.nhs.digital.uec.api.service;

import java.util.List;
import uk.nhs.digital.uec.api.model.DosService;

/** Interface to encapsulate business logic for the searching of services */
public interface FuzzyServiceSearchServiceInterface {

  /**
   * Returns a list of {@link DosService} for the search criteria provided.
   *
   * @param searchString the search criteria to look for matching services. Services will be matched
   *     by: name public name address postcode
   * @return {@link DosService}
   */
  public List<DosService> retrieveServicesByFuzzySearch(final String searchString);
}

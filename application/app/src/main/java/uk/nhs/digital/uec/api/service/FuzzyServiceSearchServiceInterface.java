package uk.nhs.digital.uec.api.service;

import java.util.List;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.model.DosService;

/** Interface to encapsulate business logic for the searching of services */
public interface FuzzyServiceSearchServiceInterface {

  /**
   * Returns a list of {@link DosService} for the search criteria provided.
   *
   * @param searchPostcode the postcode location of the search
   * @param searchTerms the search terms to look for matching services. Services will be matched by:
   *     name public name address postcode
   * @return {@link DosService}
   */
  List<DosService> retrieveServicesByFuzzySearch(
      final String searchPostcode,
      final List<String> searchTerms,
      MultiValueMap<String, String> headers);
}

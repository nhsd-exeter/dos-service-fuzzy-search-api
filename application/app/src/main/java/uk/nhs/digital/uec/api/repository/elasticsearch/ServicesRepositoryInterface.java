package uk.nhs.digital.uec.api.repository.elasticsearch;

import java.util.List;
import uk.nhs.digital.uec.api.model.DosService;

public interface ServicesRepositoryInterface {

  /**
   * Returns a list of Dos Services that match the specified search criteria.
   *
   * @param searchTerms a list of terms to match the services to.
   * @return list of Dos Services that match the search criteria.
   */
  List<DosService> findServiceBySearchTerms(List<String> searchTerms);
}

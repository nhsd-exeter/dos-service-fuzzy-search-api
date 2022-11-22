package uk.nhs.digital.uec.api.repository.elasticsearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.DosService;

public interface CustomServicesRepositoryInterface {

  /**
   * Returns a list of Dos Services that match the specified search criteria.
   *
   * @param searchTerms a list of terms to match the services to.
   * @return list of Dos Services that match the search criteria.
   */

  List<DosService> findAllServicesByGeoLocation(
      Double searchLatitude, Double searchLongitude, Double distanceRange) throws NotFoundException;

  List<DosService> findAllServicesByGeoLocationWithSearchTerms(Double searchLatitude, Double searchLongitude, Double distanceRange,List<String> searchTerms) throws NotFoundException;
}

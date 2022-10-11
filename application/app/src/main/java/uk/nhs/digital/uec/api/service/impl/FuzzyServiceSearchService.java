package uk.nhs.digital.uec.api.service.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.service.LocationServiceInterface;

@Service
public class FuzzyServiceSearchService implements FuzzyServiceSearchServiceInterface {

  @Autowired private LocationServiceInterface locationService;

  @Autowired private CustomServicesRepositoryInterface elasticsearch;

  @Autowired private ApiRequestParams apiRequestParams;

  @Override
  public List<DosService> retrieveServicesByGeoLocation(
      String searchLatitude, String searchLongitude, Double distanceRange, List<String> searchTerms)
      throws NotFoundException, InvalidParameterException {
    boolean isSearchTermNullOrEmpty = (searchTerms == null || searchTerms.isEmpty());
    List<DosService> dosServices;
    dosServices =
        isSearchTermNullOrEmpty
            ? elasticsearch.findAllServicesByGeoLocation(
                searchLatitude, searchLongitude, distanceRange)
            : elasticsearch.findServicesByGeoLocation(
                searchTerms, searchLatitude, searchLongitude, distanceRange);

    for (DosService dosService : dosServices) {
      GeoPoint source =
          new GeoPoint(Double.parseDouble(searchLatitude), Double.parseDouble(searchLongitude));

      dosService.setDistance(locationService.distanceBetween(source, dosService.getLocation()));
    }
    Collections.sort(dosServices);
    // return max number of services, or the number of services returned. Which ever is the least.
    int serviceResultLimit = apiRequestParams.getMaxNumServicesToReturn();
    if (apiRequestParams.getMaxNumServicesToReturn() > dosServices.size()) {
      serviceResultLimit = dosServices.size();
    }
    return dosServices.subList(0, serviceResultLimit);
  }
}

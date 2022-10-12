package uk.nhs.digital.uec.api.service.impl;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.ErrorMessageEnum;
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
    boolean isValidGeoSearch =
        NumberUtils.isCreatable(searchLatitude) && NumberUtils.isCreatable(searchLongitude);

    List<DosService> dosServices;

    if (!isSearchTermNullOrEmpty) {
      dosServices = elasticsearch.findServiceBySearchTerms(searchTerms);
    } else if (isValidGeoSearch) {
      dosServices =
          elasticsearch.findAllServicesByGeoLocation(
              searchLatitude, searchLongitude, distanceRange);
    } else {
      throw new InvalidParameterException(ErrorMessageEnum.INVALID_LAT_LON_VALUES.getMessage());
    }

    for (DosService dosService : dosServices) {
      GeoPoint source =
          new GeoPoint(Double.parseDouble(searchLatitude), Double.parseDouble(searchLongitude));

      dosService.setDistance(locationService.distanceBetween(source, dosService.getLocation()));
    }

    // TODO:- Calculate using postcode api for services missing lat long values

    Collections.sort(dosServices);
    // return max number of services, or the number of services returned. Which ever is the least.
    int serviceResultLimit = apiRequestParams.getMaxNumServicesToReturn();
    if (apiRequestParams.getMaxNumServicesToReturn() > dosServices.size()) {
      serviceResultLimit = dosServices.size();
    }
    return dosServices.subList(0, serviceResultLimit);
  }
}

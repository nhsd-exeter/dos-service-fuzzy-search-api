package uk.nhs.digital.uec.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.ErrorMessageEnum;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.service.ValidationServiceInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "Fuzzy_Search_Service")
public class FuzzyServiceSearchService implements FuzzyServiceSearchServiceInterface {

  @Autowired
  private CustomServicesRepositoryInterface elasticsearch;

  @Autowired
  private ApiRequestParams apiRequestParams;

  @Autowired
  private ValidationServiceInterface validationService;

  @Override
  public List<DosService> retrieveServicesByGeoLocation(
      String searchLatitude,
      String searchLongitude,
      Double distanceRange,
      List<String> searchTerms,
      String searchPostcode)
      throws NotFoundException, InvalidParameterException {

    boolean isSearchTermNullOrEmpty = (searchTerms == null || searchTerms.isEmpty());
    boolean isValidGeoSearch = NumberUtils.isCreatable(searchLatitude) && NumberUtils.isCreatable(searchLongitude);

    List<DosService> dosServices;

    if ((!isSearchTermNullOrEmpty) && (isValidGeoSearch)) {
      validationService.validateSearchCriteria(searchTerms);
      log.info("Searching using location & search terms: {}, lat: {} lng: {}", String.join(" ", searchTerms),
          searchLatitude, searchLongitude);
      dosServices = elasticsearch.findAllServicesByGeoLocationWithSearchTerms(
          Double.parseDouble(searchLatitude),
          Double.parseDouble(searchLongitude),
          distanceRange, searchTerms);
      log.info("Found {} services", dosServices.size());
    } else if (isValidGeoSearch) {
      log.info("Searching using location {} lat {} lng", searchLatitude, searchLongitude);
      dosServices = elasticsearch.findAllServicesByGeoLocation(
          Double.parseDouble(searchLatitude),
          Double.parseDouble(searchLongitude),
          distanceRange);
      log.info("Found {} services", dosServices.size());
    } else {
      throw new InvalidParameterException(
          ErrorMessageEnum.INVALID_LAT_LON_VALUES.getMessage());
    }
    /**
     * The reason to put the validation here is not to send invalid postcodes to the
     * downstream api and causing the data issue
     * from the dos services poscodes coming as invalid e.g postcode="not available"
     */
    List<DosService> filteredDosServices = dosServices.stream()
        .filter(f -> validationService.isPostcodeValid(f.getPostcode())).collect(Collectors.toList());

    List<DosService> nonPopulatedLatLongServices = new ArrayList<>();

    for (DosService dosService : filteredDosServices) {
      if (Objects.isNull(dosService.getGeoLocation()) || Objects.isNull(searchLatitude)
          || Objects.isNull(searchLongitude)) {
        nonPopulatedLatLongServices.add(dosService);
      } else {
        if (dosService.getGeoLocation().getLon() == 0D && dosService.getGeoLocation().getLat() == 0D) {
          nonPopulatedLatLongServices.add(dosService);
        }
      }
    }

    // Clean up any duplicated values
    filteredDosServices.removeIf(f -> nonPopulatedLatLongServices.stream().anyMatch(n -> n.getId() == f.getId()));
    filteredDosServices.addAll(nonPopulatedLatLongServices);

    // return max number of services, or the number of services returned. Which ever
    // is the least.
    int serviceResultLimit = apiRequestParams.getMaxNumServicesToReturn();
    if (apiRequestParams.getMaxNumServicesToReturn() > dosServices.size()) {
      serviceResultLimit = dosServices.size();
    }
    return filteredDosServices.isEmpty() ? filteredDosServices : filteredDosServices.subList(0, serviceResultLimit);
  }

}

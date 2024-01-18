package uk.nhs.digital.uec.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.ErrorMessageEnum;
import uk.nhs.digital.uec.api.model.google.GeoLocationResponse;
import uk.nhs.digital.uec.api.model.google.GeoLocationResponseResult;
import uk.nhs.digital.uec.api.model.google.Geometry;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.service.ExternalApiHandshakeInterface;
import uk.nhs.digital.uec.api.service.DosServiceSearch;
import uk.nhs.digital.uec.api.service.LocationServiceInterface;
import uk.nhs.digital.uec.api.service.ValidationServiceInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "Fuzzy_Search_Service")
public class DosServiceSearchImpl implements DosServiceSearch {

  private static final String DOS_DATA_SOURCE = "DIRECTORY_OF_SERVICES";
  private final LocationServiceInterface locationService;
  private final CustomServicesRepositoryInterface elasticsearch;
  private final ApiRequestParams apiRequestParams;
  private final ValidationServiceInterface validationService;
  private final ExternalApiHandshakeInterface externalApiHandshakeInterface;

  @Autowired
  public DosServiceSearchImpl(
    LocationServiceInterface locationService,
    CustomServicesRepositoryInterface elasticsearch,
    ApiRequestParams apiRequestParams,
    ValidationServiceInterface validationService,
    ExternalApiHandshakeInterface externalApiHandshakeInterface) {
    this.locationService = locationService;
    this.elasticsearch = elasticsearch;
    this.apiRequestParams = apiRequestParams;
    this.validationService = validationService;
    this.externalApiHandshakeInterface = externalApiHandshakeInterface;
  }


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
    boolean isValidPostcode = searchPostcode != null && validationService.isPostcodeValid(searchPostcode);

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
    } else if (isValidPostcode) {
      GeoPoint geoPoint = getGeoLocation(searchPostcode);
      log.info("Used Google API to get the GeoPoint: {} values for a given postcode: {}", geoPoint, searchPostcode);
      dosServices = isSearchTermNullOrEmpty ? elasticsearch.findAllServicesByGeoLocation(
        geoPoint.getLat(),
        geoPoint.getLon(),
        distanceRange)
        : elasticsearch.findAllServicesByGeoLocationWithSearchTerms(
        geoPoint.getLat(),
        geoPoint.getLon(),
        distanceRange, searchTerms);
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

    List<DosService> notValidPostCodeList = dosServices.stream()
      .filter(element -> !filteredDosServices.contains(element))
      .toList();

    notValidPostCodeList.forEach(e -> log.info("not valid postcode {} odscode: {}", e.getName(), e.getOdsCode()));

    List<DosService> nonPopulatedLatLongServices = new ArrayList<>();

    for (DosService dosService : filteredDosServices) {
      if ((Objects.isNull(dosService.getLocation()))
        || (dosService.getLocation().getLon() == 0D && dosService.getLocation().getLat() == 0D)) {
        nonPopulatedLatLongServices.add(dosService);
      }
      dosService.setDatasource(DOS_DATA_SOURCE);
    }
    this.populateServiceDistancesWithLatAndLng(nonPopulatedLatLongServices, searchLongitude);
    // Clean up any duplicated values
    filteredDosServices.removeIf(f -> nonPopulatedLatLongServices.stream().anyMatch(n -> n.getId() == f.getId()));
    filteredDosServices.addAll(nonPopulatedLatLongServices);

    // return max number of services, or the number of services returned. Which ever
    // is the least.
    int serviceResultLimit = apiRequestParams.getMaxNumServicesToReturn() / 2;
    if (apiRequestParams.getMaxNumServicesToReturn() > dosServices.size()) {
      serviceResultLimit = dosServices.size();
    }
    return filteredDosServices.isEmpty() ? filteredDosServices : filteredDosServices.subList(0, serviceResultLimit);
  }


  private List<DosService> populateServiceDistancesWithLatAndLng(List<DosService> nonPopulatedLatLongServices, String searchLongitude) throws InvalidParameterException, NotFoundException {
    log.info("nonPopulatedLatLong {}", nonPopulatedLatLongServices.size());
    /**
     * if dos services returns empty locations populate location based on postcodes from postcode
     * mapping API
     */
    if (!nonPopulatedLatLongServices.isEmpty()) {
      log.info("Populating services without lat and long values");
      for (DosService dosService : nonPopulatedLatLongServices) {
        GeoPoint destinationGeoPoint = getGeoLocation(dosService.getPostcode());
        dosService.setDistance(locationService.distanceBetween(new GeoPoint(Double.parseDouble(searchLongitude), Double.parseDouble(searchLongitude)), destinationGeoPoint));
      }
    }
    return nonPopulatedLatLongServices;
  }

  private GeoPoint getGeoLocation(String postcode) throws InvalidParameterException {
    GeoLocationResponse geoLocationResponse = externalApiHandshakeInterface.getGeoCoordinates(postcode);
    if (geoLocationResponse == null || geoLocationResponse.getGeoLocationResponseResults().length <= 0) {
      throw new InvalidParameterException(
        ErrorMessageEnum.INVALID_POSTCODE.getMessage());
    }
    GeoLocationResponseResult geoLocationResponseResult = geoLocationResponse.getGeoLocationResponseResults()[0];
    Geometry geometry = geoLocationResponseResult.getGeometry();
    return new GeoPoint(geometry.getLocation().getLat(), geoLocationResponseResult.getGeometry().getLocation().getLng());
  }

}

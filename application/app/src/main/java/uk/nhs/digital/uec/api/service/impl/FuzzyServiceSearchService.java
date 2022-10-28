package uk.nhs.digital.uec.api.service.impl;

import com.amazonaws.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.ErrorMessageEnum;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.ExternalApiHandshakeInterface;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.service.LocationServiceInterface;
import uk.nhs.digital.uec.api.service.ValidationServiceInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "Fuzzy_Search_Service")
public class FuzzyServiceSearchService implements FuzzyServiceSearchServiceInterface {

  @Autowired
  private LocationServiceInterface locationService;

  @Autowired
  private CustomServicesRepositoryInterface elasticsearch;

  @Autowired
  private ApiRequestParams apiRequestParams;

  @Autowired
  private ApiUtilsServiceInterface apiUtilsService;

  @Autowired
  private ValidationServiceInterface validationService;

  @Autowired
  private ExternalApiHandshakeInterface externalApiHandshakeInterface;

  @Override
  public List<DosService> retrieveServicesByGeoLocation(
    String searchLatitude,
    String searchLongitude,
    Double distanceRange,
    List<String> searchTerms,
    String searchPostcode)
    throws NotFoundException, InvalidParameterException {

    boolean isSearchPostCodeNullOrEmpty = (searchPostcode == null || searchPostcode.isEmpty());
    boolean isSearchTermNullOrEmpty = (searchTerms == null || searchTerms.isEmpty());
    boolean isValidGeoSearch = NumberUtils.isCreatable(searchLatitude) && NumberUtils.isCreatable(searchLongitude);

    List<DosService> dosServices;

    if ((!isSearchTermNullOrEmpty) && (isValidGeoSearch)) {
      validationService.validateSearchCriteria(searchTerms);
      log.info("Searching using location & search terms: {}, lat: {} lng: {}", String.join(" ", searchTerms), searchLatitude, searchLongitude);
      dosServices =
        elasticsearch.findAllServicesByGeoLocationAndSearchTerms(
          Double.parseDouble(searchLatitude),
          Double.parseDouble(searchLongitude),
          distanceRange, searchTerms);
    } else if (isValidGeoSearch) {
      log.info("Searching using location {}lat {} long", searchLatitude, searchLongitude);
      dosServices =
        elasticsearch.findAllServicesByGeoLocation(
          Double.parseDouble(searchLatitude),
          Double.parseDouble(searchLongitude),
          distanceRange);

    } else if (!isSearchPostCodeNullOrEmpty) {
      List<String> newSearchTerms = new ArrayList<>();
      newSearchTerms.add(searchPostcode);
      if (!isSearchTermNullOrEmpty) {
        newSearchTerms.addAll(searchTerms);
      }
      log.info("Searching using search terms: {}", newSearchTerms);
      validationService.validateSearchCriteria(newSearchTerms);
      List<String> sanitiseSearchTerms = apiUtilsService.sanitiseSearchTerms(newSearchTerms);
      log.info("sanitiseSearch terms : {}", sanitiseSearchTerms);
      dosServices =
        elasticsearch.findServiceBySearchTerms(sanitiseSearchTerms);
    } else {
      throw new InvalidParameterException(
        ErrorMessageEnum.INVALID_LAT_LON_VALUES_OR_INVALID_POSTCODE.getMessage());
    }
    /**
     *  The reason to put the validation here is not to send invalid postcodes to the downstream api and causing the data issue
     *   from the dos services poscodes coming as invalid e.g postcode="not available"
     * */
    List<DosService> filteredDosServices = dosServices.stream().filter(f -> validationService.isPostcodeValid(f.getPostcode())).collect(Collectors.toList());

    List<DosService> nonPopulatedLatLongServices = new ArrayList<>();

    for (DosService dosService : filteredDosServices) {

      if (Objects.isNull(dosService.getLocation()) || Objects.isNull(searchLatitude) || Objects.isNull(searchLongitude)) {
        nonPopulatedLatLongServices.add(dosService);
      } else {
        GeoPoint source =
          new GeoPoint(Double.parseDouble(searchLatitude), Double.parseDouble(searchLongitude));
        if (dosService.getLocation().getLon() == 0D && dosService.getLocation().getLat() == 0D) {
          nonPopulatedLatLongServices.add(dosService);
        }
        dosService.setDistance(locationService.distanceBetween(source, dosService.getLocation()));
      }
    }

    this.populateServiceDistancesWithNorthingAndEastings(nonPopulatedLatLongServices, searchPostcode);
    //Clean up any duplicated values
    filteredDosServices.removeIf(f -> nonPopulatedLatLongServices.stream().anyMatch(n -> n.getId() == f.getId()));
    filteredDosServices.addAll(nonPopulatedLatLongServices);
    Collections.sort(filteredDosServices);

    // return max number of services, or the number of services returned. Which ever is the least.
    int serviceResultLimit = apiRequestParams.getMaxNumServicesToReturn();
    if (apiRequestParams.getMaxNumServicesToReturn() > dosServices.size()) {
      serviceResultLimit = dosServices.size();
    }
    return filteredDosServices.isEmpty() ? filteredDosServices : filteredDosServices.subList(0, serviceResultLimit);
  }

  private List<DosService> populateServiceDistancesWithNorthingAndEastings(List<DosService> nonPopulatedLatLongServices, String searchPostcode) throws InvalidParameterException, NotFoundException {
    log.debug("nonPopulatedLatLong {}", nonPopulatedLatLongServices.size());
    /**
     * if dos services returns empty locations populate location based on postcodes from postcode
     * mapping API
     */
    if (!nonPopulatedLatLongServices.isEmpty()) {
      log.info("Populating services without lat and long values");
      /** Call the auth service login endpoint from here and get the authenticated headers */
      MultiValueMap<String, String> headers = externalApiHandshakeInterface.getAccessTokenHeader();
      log.info("Calling Postcode API to get location values");
      /** Calculate distance to services returned if we have a search location */
      PostcodeLocation searchLocation =
        locationService.getLocationForPostcode(searchPostcode, headers);

      List<PostcodeLocation> dosServicePostCodeLocation =
        this.populateEmptyLocation(nonPopulatedLatLongServices, headers);
      if (searchLocation != null) {
        for (DosService dosService : nonPopulatedLatLongServices) {
          PostcodeLocation serviceLocation = new PostcodeLocation();
          serviceLocation.setPostcode(dosService.getPostcode());
          serviceLocation.setEasting(dosService.getEasting());
          serviceLocation.setNorthing(dosService.getNorthing());

          if (serviceLocation.getEasting() == null && serviceLocation.getNorthing() == null) {
            setServiceLocation(serviceLocation, dosServicePostCodeLocation);
          }
          dosService.setDistance(locationService.distanceBetween(searchLocation, serviceLocation));
        }
      }
    }
    return nonPopulatedLatLongServices;
  }

  private void setServiceLocation(
    PostcodeLocation serviceLocation, List<PostcodeLocation> dosServicePostCodeLocation) {
    if (dosServicePostCodeLocation != null) {
      String servicePostcodeWithoutSpace =
        apiUtilsService.removeBlankSpaces(serviceLocation.getPostcode());
      serviceLocation.setEasting(
        dosServicePostCodeLocation.stream()
          .filter(t -> t.getPostcode().equals(servicePostcodeWithoutSpace))
          .map(PostcodeLocation::getEasting)
          .findFirst()
          .orElse(null));
      serviceLocation.setNorthing(
        dosServicePostCodeLocation.stream()
          .filter(t -> t.getPostcode().equals(servicePostcodeWithoutSpace))
          .map(PostcodeLocation::getNorthing)
          .findFirst()
          .orElse(null));
    }
  }

  private List<PostcodeLocation> populateEmptyLocation(
    List<DosService> dosServices, MultiValueMap<String, String> headers)
    throws InvalidParameterException {
    log.info("Populating empty location on service");
    List<String> postCodes =
      dosServices.stream()
        .filter(t -> t.getEasting() == null && t.getNorthing() == null)
        .map(DosService::getPostcode)
        .collect(Collectors.toList());
    return !CollectionUtils.isNullOrEmpty(postCodes)
      ? locationService.getLocationsForPostcodes(postCodes, headers)
      : Collections.emptyList();
  }
}

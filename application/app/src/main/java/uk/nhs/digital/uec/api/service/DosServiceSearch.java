package uk.nhs.digital.uec.api.service;

import java.util.List;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.DosService;

/** Interface to encapsulate business logic for the searching of services */
public interface DosServiceSearch {
  /**
   * Returns a list of {@link DosService} for the search criteria provided.
   *
   * @param searchLatitude the latitude of the search
   * @param searchLongitude the longitude of the search
   * @param searchTerms the search terms to look for matching services. Services will be matched by:
   *     name public name address postcode
   * @return {@link DosService}
   * @throws NotFoundException
   * @throws InvalidParameterException
   */
  List<DosService> retrieveServicesByGeoLocation(
      final String searchLatitude,
      final String searchLongitude,
      final Double distanceRange,
      final List<String> searchTerms,
      final String searchPostcode)
      throws NotFoundException, InvalidParameterException;

  List<DosService> retrieveServices(
      String searchLatitude,
      String searchLongitude,
      Double distanceRange,
      List<String> searchTerms,
      String searchPostcode)
      throws InvalidParameterException, NotFoundException;
}

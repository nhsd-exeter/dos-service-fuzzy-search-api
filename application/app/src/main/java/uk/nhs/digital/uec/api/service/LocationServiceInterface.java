package uk.nhs.digital.uec.api.service;

import java.util.List;

import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.PostcodeLocation;

public interface LocationServiceInterface {

  /**
   * Retrieve PostcodeLocation (easting and northing values) for a given postcode.
   *
   * @param postcode postcode to retrieve the {@link PostcodeLocation} for.
   * @return {@link PostcodeLocation}
   * @throws NotFoundException
   * @throws InvalidParameterException
   */
  PostcodeLocation getLocationForPostcode(
      final String postcode, MultiValueMap<String, String> headers)
      throws NotFoundException, InvalidParameterException;

  /**
   * Returns the point-to-point distance (in miles) between the source and destination locations.
   *
   * @param source location source
   * @param destination location destination
   * @return point-to-point distance (in miles) between the source and destination locations.
   */
  Double distanceBetween(PostcodeLocation source, PostcodeLocation destination);
  /**
   * Returns the point-to-point distance (in miles) between the source and destination locations.
   *
   * @param source location source
   * @param destination location destination
   * @return point-to-point distance (in miles) between the source and destination locations.
   */
  Double distanceBetween(GeoPoint source, GeoPoint destination);

  /**
   * Retrieve PostcodeLocation (easting and northing values) for a given postcodes.
   *
   * @param postcodes postcodes to retrieve the {@link PostcodeLocation} for.
   * @return {@link List of PostcodeLocation
   *
   * @throws InvalidParameterException   */
  List<PostcodeLocation> getLocationsForPostcodes(
      final List<String> postcodes, MultiValueMap<String, String> headers)
      throws InvalidParameterException;
}

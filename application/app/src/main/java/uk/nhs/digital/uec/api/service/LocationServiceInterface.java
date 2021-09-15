package uk.nhs.digital.uec.api.service;

import java.util.List;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.model.PostcodeLocation;

public interface LocationServiceInterface {

  /**
   * Retrieve PostcodeLocation (easting and northing values) for a given postcode.
   *
   * @param postcode postcode to retrieve the {@link PostcodeLocation} for.
   * @return {@link PostcodeLocation}
   */
  PostcodeLocation getLocationForPostcode(
      final String postcode, MultiValueMap<String, String> headers);

  /**
   * Returns the point-to-point distance (in miles) between the source and destination locations.
   *
   * @param source location source
   * @param destination location destination
   * @return point-to-point distance (in miles) between the source and destination locations.
   */
  Double distanceBetween(PostcodeLocation source, PostcodeLocation destination);
  /**
   * Retrieve PostcodeLocation (easting and northing values) for a given postcodes.
   *
   * @param postcodes postcodes to retrieve the {@link PostcodeLocation} for.
   * @return {@link List of PostcodeLocation
   */
  List<PostcodeLocation> getLocationsForPostcodes(
      final List<String> postcodes, MultiValueMap<String, String> headers);
}

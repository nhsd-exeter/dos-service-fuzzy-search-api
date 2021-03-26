package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.model.dynamo.PostcodeLocation;

public interface LocationServiceInterface {

  /**
   * Retrieve PostcodeLocation (easting and northing values) for a given postcode.
   *
   * @param postcode postcode to retrieve the {@link PostcodeLocation} for.
   * @return {@link PostcodeLocation}
   */
  PostcodeLocation getLocationForPostcode(final String postcode);

  /**
   * Returns the point-to-point distance (in miles) between the source and destination locations.
   *
   * @param source location source
   * @param destination location destination
   * @return point-to-point distance (in miles) between the source and destination locations.
   */
  Double distanceBetween(PostcodeLocation source, PostcodeLocation destination);
}

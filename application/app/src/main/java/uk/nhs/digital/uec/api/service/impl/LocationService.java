package uk.nhs.digital.uec.api.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.ExternalApiHandshakeInterface;
import uk.nhs.digital.uec.api.service.LocationServiceInterface;

@Service
@Slf4j
public class LocationService implements LocationServiceInterface {

  private final ApiUtilsServiceInterface apiUtilsService;
  private final ExternalApiHandshakeInterface apiHandshakeService;

  @Autowired
  public LocationService(
    ApiUtilsServiceInterface apiUtilsService,
    ExternalApiHandshakeInterface apiHandshakeService
  ){
    this.apiUtilsService = apiUtilsService;
    this.apiHandshakeService = apiHandshakeService;
  }

  /**
   * {@inheritDoc}
   *
   * @throws NotFoundException
   * @throws InvalidParameterException
   */
  @Override
  public PostcodeLocation getLocationForPostcode(
      final String postcode, MultiValueMap<String, String> headers)
      throws NotFoundException, InvalidParameterException {
    log.info("Sending postcode downstream - postcode: {}", postcode);

    try {
      if (StringUtils.isBlank(postcode)) {
        return null;
      }
      List<String> sanitisedPostcodes =
          apiUtilsService.removeBlankSpacesIn(Stream.of(postcode).collect(Collectors.toList()));

      log.info("Sending postcode downstream after sanitised - postcode: {}", sanitisedPostcodes);

      List<PostcodeLocation> postcodeMappings =
          apiHandshakeService.getPostcodeMappings(sanitisedPostcodes, headers);
      return postcodeMappings.stream().findFirst().orElse(new PostcodeLocation());
    } catch (Exception e) {
      log.error("An error occurred when accessing the postcode service, {}", e.getMessage());
    }
    log.warn("Returning empty postcode location for {} value", postcode);
    return new PostcodeLocation();
  }

  /** {@inheritDoc} */
  @Override
  public Double distanceBetween(PostcodeLocation source, PostcodeLocation destination) {
    Double distance = null;

    if (source == null
        || destination == null
        || source.getEasting() == null
        || source.getNorthing() == null
        || destination.getEasting() == null
        || destination.getNorthing() == null) {
      return distance;
    }

    Double eastingDiff = Math.pow((source.getEasting() - destination.getEasting()), 2);
    Double northingDiff = Math.pow((source.getNorthing() - destination.getNorthing()), 2);

    double distanceInMetres = Math.sqrt(eastingDiff + northingDiff);

    double distanceInMiles = distanceInMetres / 1609;

    return DoubleRounder.round(distanceInMiles, 1);
  }

  @Override
  public List<PostcodeLocation> getLocationsForPostcodes(
      List<String> postCodes, MultiValueMap<String, String> headers)
      throws InvalidParameterException {
    postCodes.forEach(pc -> log.info("Getting location for {}", pc));
    return apiHandshakeService.getPostcodeMappings(
        apiUtilsService.removeBlankSpacesIn(postCodes), headers);
  }

  @Override
  public Double distanceBetween(GeoPoint source, GeoPoint destination) {
    if (source == null || destination == null) {
      return null;
    }
    if ((source.getLat() == destination.getLat()) && (source.getLon() == destination.getLon())) {
      return 0.0;
    } else {
      double theta = source.getLon() - destination.getLon();
      double dist =
          Math.sin(Math.toRadians(source.getLat())) * Math.sin(Math.toRadians(destination.getLat()))
              + Math.cos(Math.toRadians(source.getLat()))
                  * Math.cos(Math.toRadians(destination.getLat()))
                  * Math.cos(Math.toRadians(theta));
      dist = Math.acos(dist);
      dist = Math.toDegrees(dist);
      dist = dist * 60 * 1.1515;
      return DoubleRounder.round(dist, 1);
    }
  }
}

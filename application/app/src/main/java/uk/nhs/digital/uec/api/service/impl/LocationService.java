package uk.nhs.digital.uec.api.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.LocationServiceInterface;
import uk.nhs.digital.uec.api.util.PostcodeMappingUtil;

@Service
public class LocationService implements LocationServiceInterface {

  @Autowired private ApiUtilsServiceInterface apiUtilsService;

  @Autowired private PostcodeMappingUtil postcodeMappingUtil;

  /** {@inheritDoc} */
  @Override
  public PostcodeLocation getLocationForPostcode(
      final String postcode, MultiValueMap<String, String> headers) {
    if (StringUtils.isBlank(postcode)) {
      return null;
    }
    List<PostcodeLocation> postcodeMappings =
        postcodeMappingUtil.getPostcodeMappings(
            apiUtilsService.removeBlankSpacesIn(Stream.of(postcode).toList()), headers);
    Optional<PostcodeLocation> postcodeLocation = postcodeMappings.stream().findFirst();
    return postcodeLocation.isPresent() ? postcodeLocation.get() : null;
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

    Double distanceInMetres = Math.sqrt(eastingDiff + northingDiff);

    Double distanceInMiles = distanceInMetres / 1609;

    return DoubleRounder.round(distanceInMiles, 1);
  }

  @Override
  public List<PostcodeLocation> getLocationsForPostcodes(
      List<String> postCodes, MultiValueMap<String, String> headers) {
    return postcodeMappingUtil.getPostcodeMappings(
        apiUtilsService.removeBlankSpacesIn(postCodes), headers);
  }
}

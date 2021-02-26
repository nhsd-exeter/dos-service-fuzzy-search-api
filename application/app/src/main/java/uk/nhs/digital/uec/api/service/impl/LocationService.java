package uk.nhs.digital.uec.api.service.impl;

import java.util.List;
import java.util.Optional;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.dynamo.PostcodeLocation;
import uk.nhs.digital.uec.api.repository.dynamo.PostcodeLocationRepo;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.LocationServiceInterface;

@Service
public class LocationService implements LocationServiceInterface {

  @Autowired private ApiUtilsServiceInterface apiUtilsService;

  @Autowired private PostcodeLocationRepo postcodeLocationRepo;

  /** {@inheritDoc} */
  @Override
  public PostcodeLocation getLocationForPostcode(final String postcode) {

    PostcodeLocation location = null;

    if (postcode == null) {
      return location;
    }

    List<Optional<PostcodeLocation>> locationResult =
        postcodeLocationRepo.findByPostcode(apiUtilsService.removeBlankSpaces(postcode));

    if (!locationResult.isEmpty() && locationResult.get(0).isPresent()) {
      location = locationResult.get(0).get();
    }

    return location;
  }

  /** {@inheritDoc} */
  @Override
  public Double distanceBetween(PostcodeLocation source, PostcodeLocation destination) {

    Double distance = null;

    if (source == null || destination == null) {
      return distance;
    }

    Double eastingDiff = Math.pow((source.getEasting() - destination.getEasting()), 2);
    Double northingDiff = Math.pow((source.getNorthing() - destination.getNorthing()), 2);

    Double distanceInMetres = Math.sqrt(eastingDiff + northingDiff);

    Double distanceInMiles = distanceInMetres / 1609;

    return DoubleRounder.round(distanceInMiles, 1);
  }
}

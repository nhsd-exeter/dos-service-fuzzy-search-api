package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.dynamo.PostcodeLocation;
import uk.nhs.digital.uec.api.repository.dynamo.PostcodeLocationRepo;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;

@Service
public class FuzzyServiceSearchService implements FuzzyServiceSearchServiceInterface {

  @Autowired private ApiUtilsServiceInterface apiUtilsService;

  @Autowired private CustomServicesRepositoryInterface elasticsearch;

  @Autowired private PostcodeLocationRepo postcodeLocationRepo;

  /** {@inheritDoc} */
  @Override
  public List<DosService> retrieveServicesByFuzzySearch(
      final String searchPostcode, final List<String> searchTerms) {

    final List<DosService> dosServices = new ArrayList<DosService>();
    dosServices.addAll(
        elasticsearch.findServiceBySearchTerms(apiUtilsService.sanitiseSearchTerms(searchTerms)));

    for (DosService dosService : dosServices) {
      dosService.setDistance(
          retrieveDistance(
              getLocationForPostcode(searchPostcode),
              getLocationForPostcode(dosService.getPostcode())));
    }

    Collections.sort(dosServices);

    return dosServices;
  }

  private PostcodeLocation getLocationForPostcode(final String postcode) {

    PostcodeLocation location = null;

    List<Optional<PostcodeLocation>> locationResult =
        postcodeLocationRepo.findByPostcode(apiUtilsService.removeBlankSpaces(postcode));

    if (!locationResult.isEmpty() && locationResult.get(0).isPresent()) {
      location = locationResult.get(0).get();
    }

    return location;
  }

  private Double retrieveDistance(PostcodeLocation source, PostcodeLocation destination) {

    Double distance = 99.00;

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

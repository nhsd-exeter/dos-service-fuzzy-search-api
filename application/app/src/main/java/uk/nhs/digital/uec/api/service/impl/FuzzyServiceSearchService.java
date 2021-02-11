package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;

@Service
public class FuzzyServiceSearchService implements FuzzyServiceSearchServiceInterface {

  @Autowired private ApiUtilsServiceInterface apiUtilsService;

  @Autowired private CustomServicesRepositoryInterface elasticsearch;

  /** {@inheritDoc} */
  @Override
  public List<DosService> retrieveServicesByFuzzySearch(final List<String> searchTerms) {

    final List<DosService> dosServices = new ArrayList<>();

    dosServices.addAll(
        elasticsearch.findServiceBySearchTerms(apiUtilsService.sanitiseSearchTerms(searchTerms)));

    return dosServices;
  }
}

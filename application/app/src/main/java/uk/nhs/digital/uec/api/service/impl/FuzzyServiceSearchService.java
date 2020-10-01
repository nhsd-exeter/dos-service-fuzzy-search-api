package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;

@Service
public class FuzzyServiceSearchService implements FuzzyServiceSearchServiceInterface {

  @Autowired private ServicesRepositoryInterface elasticsearch;

  @Value("${param.services.max_num_services_to_return}")
  private int maxNumServicesToReturn;

  /** {@inheritDoc} */
  @Override
  public List<DosService> retrieveServicesByFuzzySearch(final List<String> searchTerms) {

    final List<DosService> dosServices = new ArrayList<>();
    if (elasticsearch.findServiceBySearchTerms(searchTerms) != null) {
      dosServices.addAll(elasticsearch.findServiceBySearchTerms(searchTerms));
    }

    if (dosServices.size() > maxNumServicesToReturn) {
      return dosServices.subList(0, maxNumServicesToReturn);
    }

    return dosServices;
  }
}

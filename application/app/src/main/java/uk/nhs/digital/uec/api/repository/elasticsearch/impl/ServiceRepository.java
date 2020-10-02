package uk.nhs.digital.uec.api.repository.elasticsearch.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;

@Repository
public class ServiceRepository implements CustomServicesRepositoryInterface {

  @Autowired private ServicesRepositoryInterface servicesRepo;

  @Autowired private ApiRequestParams request;

  /** {@inheritDoc} */
  @Override
  public List<DosService> findServiceBySearchTerms(List<String> searchTerms) {
    final List<DosService> dosServices = new ArrayList<>();

    Iterable<DosService> services =
        servicesRepo.findByName(searchTerms.get(0), request.getFuzzLevel());

    Iterator<DosService> serviceit = services.iterator();
    while (serviceit.hasNext()) {
      dosServices.add(serviceit.next());
    }

    return dosServices;
  }
}

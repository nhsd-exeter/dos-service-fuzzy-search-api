package uk.nhs.digital.uec.api.repository.elasticsearch.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;
import uk.nhs.digital.uec.api.service.impl.MockDosServicesUtil;

@Profile("prod")
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

  @Override
  public void saveMockServices() {
    MockDosServicesUtil.addMockServices(10);

    for (int i = 1; i <= 10; i++) {
      servicesRepo.save(MockDosServicesUtil.mockDosServices.get(i));
    }
  }
}

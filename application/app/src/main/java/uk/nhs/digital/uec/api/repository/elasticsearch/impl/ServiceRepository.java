package uk.nhs.digital.uec.api.repository.elasticsearch.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;

@Repository
@Slf4j
public class ServiceRepository implements CustomServicesRepositoryInterface {

  @Autowired private ServicesRepositoryInterface servicesRepo;

  @Autowired private ApiRequestParams apiRequestParams;

  /** {@inheritDoc} */
  @Override
  public List<DosService> findServiceBySearchTerms(List<String> searchTerms) {
    final List<DosService> dosServices = new ArrayList<>();

    log.info("Request Params: " + apiRequestParams.getFuzzLevel());
    log.info(
        "Number of services to get from elasticsearch: "
            + apiRequestParams.getMaxNumServicesToReturnFromElasticsearch());

    Iterable<DosService> services =
        servicesRepo.findBySearchTerms(
            searchTerms.get(0),
            apiRequestParams.getFuzzLevel(),
            apiRequestParams.getNamePriority(),
            apiRequestParams.getAddressPriority(),
            apiRequestParams.getPostcodePriority(),
            apiRequestParams.getPublicNamePriority(),
            PageRequest.of(0, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()));

    Iterator<DosService> serviceit = services.iterator();
    while (serviceit.hasNext()) {
      dosServices.add(serviceit.next());
    }

    return dosServices;
  }
}
